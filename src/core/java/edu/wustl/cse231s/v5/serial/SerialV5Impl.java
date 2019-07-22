/*******************************************************************************
 * Copyright (C) 2016-2018 Dennis Cosgrove
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package edu.wustl.cse231s.v5.serial;

import java.util.Optional;
import java.util.Spliterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;

import edu.wustl.cse231s.v5.api.AccumulatorReducer;
import edu.wustl.cse231s.v5.api.Activity;
import edu.wustl.cse231s.v5.api.Bookkeeping;
import edu.wustl.cse231s.v5.api.CheckedCallable;
import edu.wustl.cse231s.v5.api.CheckedConsumer;
import edu.wustl.cse231s.v5.api.CheckedRunnable;
import edu.wustl.cse231s.v5.api.ContentionLevel;
import edu.wustl.cse231s.v5.api.DoubleAccumulationDeterminismPolicy;
import edu.wustl.cse231s.v5.api.FinishAccumulator;
import edu.wustl.cse231s.v5.api.Metrics;
import edu.wustl.cse231s.v5.api.NumberReductionOperator;
import edu.wustl.cse231s.v5.impl.AbstractV5Impl;
import edu.wustl.cse231s.v5.impl.SpliteratorUtils;
import edu.wustl.cse231s.v5.options.AwaitFuturesOption;
import edu.wustl.cse231s.v5.options.RegisterAccumulatorsOption;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class SerialV5Impl extends AbstractV5Impl {
	@Override
	public void shutdownNow() {
	}

	private void run(CheckedRunnable runnable) {
		try {
			runnable.run();
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException ee) {
			Throwable cause = ee.getCause();
			if (cause instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			} else if (cause instanceof RuntimeException) {
				RuntimeException runtimeException = (RuntimeException) cause;
				throw runtimeException;
			} else if (cause instanceof Error) {
				Error error = (Error) cause;
				throw error;
			} else {
				throw new RuntimeException(ee);
			}
		}
	}

	@Override
	public void finish(CheckedRunnable body) throws InterruptedException, ExecutionException {
		body.run();
		if (Thread.interrupted()) {
			throw new InterruptedException();
		}
	}

	@Override
	public void async(CheckedRunnable body) {
		run(body);
	}

	private static <R> Future<R> canceledFuture(InterruptedException ie) {
		return new Future<R>() {
			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				return false;
			}

			@Override
			public boolean isCancelled() {
				return true;
			}

			@Override
			public boolean isDone() {
				return false;
			}

			@Override
			public R get() throws InterruptedException, ExecutionException {
				throw ie;
			}

			@Override
			public R get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException, TimeoutException {
				throw ie;
			}
		};
	}

	@Override
	public <R> Future<R> future(CheckedCallable<R> body) {
		try {
			R value = body.call();
			return ConcurrentUtils.constantFuture(value);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
			return canceledFuture(ie);
		} catch (ExecutionException ee) {
			Throwable cause = ee.getCause();
			if (cause instanceof InterruptedException) {
				Thread.currentThread().interrupt();
				return canceledFuture((InterruptedException) cause);
			} else {
				throw ConcurrentUtils.extractCauseUnchecked(ee);
			}
		}
	}

	@Override
	public void finish(RegisterAccumulatorsOption registerAccumulatorsOption, CheckedRunnable body)
			throws InterruptedException, ExecutionException {
		finish(body);
	}

	@Override
	public FinishAccumulator<Integer> newIntegerFinishAccumulator(NumberReductionOperator operator,
			ContentionLevel contentionLevel) {
		return new SerialIntFinishAccumulator(operator);
	}

	@Override
	public FinishAccumulator<Double> newDoubleFinishAccumulator(NumberReductionOperator operator,
			ContentionLevel contentionLevel, DoubleAccumulationDeterminismPolicy determinismPolicy) {
		throw new RuntimeException("todo");
	}

	@Override
	public <T> FinishAccumulator<T> newReducerFinishAccumulator(AccumulatorReducer<T> reducer,
			ContentionLevel contentionLevel) {
		throw new RuntimeException("todo");
	}

	@Override
	public <R> Future<R> future(AwaitFuturesOption awaitFuturesOption, CheckedCallable<R> body) {
		for (Future<?> future : awaitFuturesOption.getFutures()) {
			// TODO: investigate
			try {
				future.get();
			} catch (InterruptedException ie) {
				return canceledFuture(ie);
			} catch (ExecutionException ee) {
				Throwable cause = ee.getCause();
				if (cause instanceof InterruptedException) {
					Thread.currentThread().interrupt();
					return canceledFuture((InterruptedException) cause);
				} else {
					throw ConcurrentUtils.extractCauseUnchecked(ee);
				}
			}
		}
		return future(body);
	}

	@Override
	protected void asyncTasks(CheckedRunnable[] tasks) throws InterruptedException, ExecutionException {
		for (CheckedRunnable task : tasks) {
			task.run();
		}
	}

	@Override
	protected <T> void split(Spliterator<T> spliterator, long threshold, CheckedConsumer<T> body)
			throws InterruptedException, ExecutionException {
		SpliteratorUtils.forEachRemaining(spliterator, body);
	}

	@Override
	public int numWorkerThreads() {
		return 1;
	}

	@Override
	public void doWork(long n) {
	}

	@Override
	public Metrics getMetrics() {
		return new Metrics() {
			@Override
			public Optional<Long> totalWork() {
				return Optional.empty();
			}

			@Override
			public Optional<Long> criticalPathLength() {
				return Optional.empty();
			}
		};
	}

	@Override
	public Optional<Activity> getCurrentActivity() {
		return Optional.empty();
	}

	@Override
	public void resetBookkeeping() {
	}

	@Override
	public Optional<Bookkeeping> getBookkeeping() {
		return Optional.empty();
	}
}

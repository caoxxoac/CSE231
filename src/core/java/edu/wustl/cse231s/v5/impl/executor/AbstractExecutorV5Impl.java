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
 *******************************************************************************/
package edu.wustl.cse231s.v5.impl.executor;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Spliterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import edu.wustl.cse231s.v5.api.AccumulatorReducer;
import edu.wustl.cse231s.v5.api.CheckedCallable;
import edu.wustl.cse231s.v5.api.CheckedConsumer;
import edu.wustl.cse231s.v5.api.CheckedRunnable;
import edu.wustl.cse231s.v5.api.ContentionLevel;
import edu.wustl.cse231s.v5.api.DoubleAccumulationDeterminismPolicy;
import edu.wustl.cse231s.v5.api.FinishAccumulator;
import edu.wustl.cse231s.v5.api.NumberReductionOperator;
import edu.wustl.cse231s.v5.impl.AbstractV5Impl;
import edu.wustl.cse231s.v5.impl.SpliteratorUtils;
import edu.wustl.cse231s.v5.options.RegisterAccumulatorsOption;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public abstract class AbstractExecutorV5Impl extends AbstractV5Impl {
	private final ExecutorService executorService;

	public AbstractExecutorV5Impl(ExecutorService executorService) {
		this.executorService = executorService;
	}

	@Override
	public void shutdownNow() {
		this.executorService.shutdownNow();
	}

	protected abstract FinishContext createFinishContext(Context context,
			RegisterAccumulatorsOption registerAccumulatorsOption);

	@Override
	public void finish(RegisterAccumulatorsOption registerAccumulatorsOption, CheckedRunnable body)
			throws InterruptedException, ExecutionException {
		Deque<Context> stack = contextStack.get();
		FinishContext context = this.createFinishContext(stack.peek(), registerAccumulatorsOption);
		stack.push(context);
		try {
			body.run();
			context.joinAllTasks();
		} finally {
			stack.pop();
		}
		context.commitAccumulatorsIfNecessary();
	}

	private void asyncRangeOfTasks(Context context, CheckedRunnable[] tasks, int min, int maxExclusive)
			throws InterruptedException, ExecutionException {
		int rangeLength = maxExclusive - min;
		if (rangeLength > 1) {
			int mid = (maxExclusive + min) / 2;
			context.submitRunnable(executorService, () -> asyncRangeOfTasks(context, tasks, min, mid));
			asyncRangeOfTasks(context, tasks, mid, maxExclusive);
		} else {
			tasks[min].run();
		}
	}

	@Override
	protected void asyncTasks(CheckedRunnable[] tasks) throws InterruptedException, ExecutionException {
		Objects.requireNonNull(tasks);
		for (CheckedRunnable task : tasks) {
			Objects.requireNonNull(task);
		}
		Context context = contextStack.get().peek();
		asyncRangeOfTasks(context, tasks, 0, tasks.length);
	}

	@Override
	protected <T> void split(Spliterator<T> spliterator, long threshold, CheckedConsumer<T> body)
			throws InterruptedException, ExecutionException {
		if (threshold > 1L) {
			if (spliterator.estimateSize() <= threshold) {
				SpliteratorUtils.forEachRemaining(spliterator, body);
				return;
			}
		}
		Spliterator<T> s = spliterator.trySplit();
		if (s != null) {
			Context context = contextStack.get().peek();
			context.submitRunnable(executorService, () -> split(spliterator, threshold, body));
			split(s, threshold, body);
		} else {
			if (spliterator.getExactSizeIfKnown() == 1L) {
				SpliteratorUtils.forEachRemaining(spliterator, body);
			} else {
				// TODO: handle threshold > 1L
				Context context = contextStack.get().peek();
				spliterator.forEachRemaining((v) -> {
					context.submitRunnable(executorService, () -> body.accept(v));
				});
			}
		}
	}

	@Override
	public void async(CheckedRunnable body) {
		Objects.requireNonNull(body);
		Context context = contextStack.get().peek();
		context.submitRunnable(executorService, body);
	}

	@Override
	public <R> Future<R> future(CheckedCallable<R> body) {
		Objects.requireNonNull(body);
		Context context = contextStack.get().peek();
		return context.submitCallable(executorService, body);
	}

	@Override
	public int numWorkerThreads() {
		if (executorService instanceof ForkJoinPool) {
			ForkJoinPool forkJoinPool = (ForkJoinPool) executorService;
			return forkJoinPool.getParallelism();
		} else if (executorService instanceof ThreadPoolExecutor) {
			ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService;
			return threadPoolExecutor.getMaximumPoolSize();
		} else {
			// TODO
			// return Runtime.getRuntime().availableProcessors();
			throw new RuntimeException(Objects.toString(executorService));
		}
	}

	@Override
	public void finish(CheckedRunnable body) throws InterruptedException, ExecutionException {
		finish(null, body);
	}

	@Override
	public void doWork(long n) {
		getCurrentContext().doWork(n);
	}

	@Override
	public FinishAccumulator<Integer> newIntegerFinishAccumulator(NumberReductionOperator operator,
			ContentionLevel contentionLevel) {
		if (contentionLevel == ContentionLevel.HIGH) {
			return new LazyIntegerAccumulator(operator);
		} else {
			return new EagerIntegerAccumulator(operator);
		}
	}

	@Override
	public FinishAccumulator<Double> newDoubleFinishAccumulator(NumberReductionOperator operator,
			ContentionLevel contentionLevel, DoubleAccumulationDeterminismPolicy determinismPolicy) {
		if (determinismPolicy == DoubleAccumulationDeterminismPolicy.DETERMINISTIC) {
			switch (operator) {
			case SUM:
			case PRODUCT:
				if (contentionLevel == ContentionLevel.HIGH) {
					return new LazyBigDecimalAccumulator(operator);
				} else {
					return new EagerBigDecimalAccumulator(operator);
				}
			default:
				// pass
			}
		}
		if (contentionLevel == ContentionLevel.HIGH) {
			return new LazyDoubleAccumulator(operator);
		} else {
			return new EagerDoubleAccumulator(operator);
		}
	}

	@Override
	public <T> FinishAccumulator<T> newReducerFinishAccumulator(AccumulatorReducer<T> reducer,
			ContentionLevel contentionLevel) {
		if (contentionLevel == ContentionLevel.HIGH) {
			return new LazyReducerAccumulator<>(reducer);
		} else {
			return new EagerReducerAccumulator<>(reducer);
		}
	}

	protected static ThreadLocal<Deque<Context>> contextStack = ThreadLocal.withInitial(() -> {
		return new LinkedList<>();
	});

	protected static Context getCurrentContext() {
		return contextStack.get().peek();
	}

	protected static interface Context {
		<R> Future<R> submitCallable(ExecutorService executorService, CheckedCallable<R> callable);

		Future<Void> submitRunnable(ExecutorService executorService, CheckedRunnable runnable);

		void doWork(long n);
	};

	protected static interface FinishContext extends Context {
		void joinAllTasks() throws InterruptedException, ExecutionException;

		void commitAccumulatorsIfNecessary();
	};
}

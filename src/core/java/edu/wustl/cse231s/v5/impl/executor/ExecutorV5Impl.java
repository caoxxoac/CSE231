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
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import edu.wustl.cse231s.v5.api.Activity;
import edu.wustl.cse231s.v5.api.Bookkeeping;
import edu.wustl.cse231s.v5.api.CheckedCallable;
import edu.wustl.cse231s.v5.api.CheckedRunnable;
import edu.wustl.cse231s.v5.api.FinishAccumulator;
import edu.wustl.cse231s.v5.api.Metrics;
import edu.wustl.cse231s.v5.options.AwaitFuturesOption;
import edu.wustl.cse231s.v5.options.RegisterAccumulatorsOption;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class ExecutorV5Impl extends AbstractExecutorV5Impl {
	public ExecutorV5Impl(ExecutorService executorService) {
		super(executorService);
	}

	@Override
	public <R> Future<R> future(AwaitFuturesOption awaitFuturesOption, CheckedCallable<R> body) {
		List<Future<?>> futures = awaitFuturesOption.getFutures();
		CompletableFuture<?>[] completableFutures = new CompletableFuture[futures.size()];
		int i = 0;
		for (Future<?> future : futures) {
			if (future instanceof CompletableFuture<?>) {
				completableFutures[i] = (CompletableFuture<?>) future;
			} else {
				completableFutures[i] = CompletableFuture.supplyAsync(() -> {
					try {
						return future.get();
					} catch (InterruptedException | ExecutionException e) {
						throw new RuntimeException(e);
					}
				});
			}
			i++;
		}
		// todo: push and pop context
		// todo: move to AbstractExecutorV5Impl
		Context context = contextStack.get().peek();
		CompletableFuture<R> task = CompletableFuture.allOf(completableFutures).thenApplyAsync((a) -> {
			try {
				return body.call();
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException(e);
			}
		});
		((XFinishContext) context).tasks.offer(task);
		return task;
	}

	@Override
	protected FinishContext createFinishContext(Context context,
			RegisterAccumulatorsOption registerAccumulatorsOption) {
		return new XFinishContext(
				registerAccumulatorsOption != null ? registerAccumulatorsOption.getAccumulators() : null);
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
	public Optional<Bookkeeping> getBookkeeping() {
		return Optional.empty();
	}

	@Override
	public void resetBookkeeping() {
	}

	private static class XFinishContext implements FinishContext {
		private final Queue<Future<?>> tasks = new ConcurrentLinkedQueue<>();
		private final FinishAccumulator<?>[] accumulators;

		public XFinishContext(FinishAccumulator<?>[] accumulators) {
			this.accumulators = accumulators;
			if (this.accumulators != null) {
				for (FinishAccumulator<?> a : this.accumulators) {
					Accumulator<?> acc = (Accumulator<?>) a;
					acc.open();
				}
			}
		}

		@Override
		public Future<Void> submitRunnable(ExecutorService executorService, CheckedRunnable body) {
			Future<Void> task = executorService.submit(() -> {
				Deque<Context> stack = contextStack.get();
				stack.push(this);
				try {
					body.run();
				} finally {
					stack.pop();
				}
				return null;
			});
			tasks.offer(task);
			return task;
		}

		@Override
		public <R> Future<R> submitCallable(ExecutorService executorService, CheckedCallable<R> body) {
			Future<R> task = executorService.submit(() -> {
				Deque<Context> stack = contextStack.get();
				stack.push(this);
				try {
					return body.call();
				} finally {
					stack.pop();
				}
			});
			tasks.offer(task);
			return task;
		}

		@Override
		public void joinAllTasks() throws InterruptedException, ExecutionException {
			while (true) {
				if (tasks.isEmpty()) {
					break;
				} else {
					tasks.poll().get();
				}
			}
		}

		@Override
		public void commitAccumulatorsIfNecessary() {
			if (this.accumulators != null) {
				for (FinishAccumulator<?> a : this.accumulators) {
					Accumulator<?> acc = (Accumulator<?>) a;
					acc.close();
				}
			}
		}

		@Override
		public void doWork(long n) {
		}
	}
}

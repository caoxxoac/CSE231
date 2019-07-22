package edu.wustl.cse231s.v5.impl.executor;

import java.util.Deque;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import edu.wustl.cse231s.v5.api.Activity;
import edu.wustl.cse231s.v5.api.Bookkeeping;
import edu.wustl.cse231s.v5.api.CheckedCallable;
import edu.wustl.cse231s.v5.api.CheckedRunnable;
import edu.wustl.cse231s.v5.api.FinishAccumulator;
import edu.wustl.cse231s.v5.api.Metrics;
import edu.wustl.cse231s.v5.options.AwaitFuturesOption;
import edu.wustl.cse231s.v5.options.RegisterAccumulatorsOption;

public abstract class AbstractActivityTrackingExecutorV5Impl extends AbstractExecutorV5Impl {
	public AbstractActivityTrackingExecutorV5Impl(ExecutorService executorService) {
		super(executorService);
	}

	@Override
	public <R> Future<R> future(AwaitFuturesOption awaitFuturesOption, CheckedCallable<R> body) {
		throw new RuntimeException("TODO");
	}

	@Override
	protected FinishContext createFinishContext(Context parent, RegisterAccumulatorsOption registerAccumulatorsOption) {
		return new FinishNode((Node) parent,
				registerAccumulatorsOption != null ? registerAccumulatorsOption.getAccumulators() : null);
	}

	@Override
	public Optional<Activity> getCurrentActivity() {
		return Optional.of((Activity) getCurrentContext());
	}

	@Override
	public Metrics getMetrics() {
		Node node = (Node) getCurrentContext();
		Node root = node.getRoot();
		long totalWork = root.totalWork();
		return new Metrics() {
			@Override
			public Optional<Long> totalWork() {
				return Optional.of(totalWork);
			}

			@Override
			public Optional<Long> criticalPathLength() {
				return Optional.empty();
			}
		};
	}

	@Override
	public Optional<Bookkeeping> getBookkeeping() {
		return Optional.empty();
	}

	private static AtomicInteger idCount = new AtomicInteger();

	private static abstract class Node implements Activity, Context {
		private final Node parent;
		private final int id;
		private final Queue<Node> allChildren = new ConcurrentLinkedQueue<>();
		private final Queue<Node> unjoinedChildren = new ConcurrentLinkedQueue<>();
		private final AtomicLong work = new AtomicLong();

		public Node(Node parent) {
			this.parent = parent;
			this.id = idCount.getAndIncrement();
			if (this.parent != null) {
				this.parent.allChildren.add(this);
				this.parent.unjoinedChildren.add(this);
			}
		}

		@Override
		public int getId() {
			return id;
		}

		@Override
		public Node getParent() {
			return parent;
		}

		public Node getRoot() {
			if (this.parent != null) {
				return this.parent.getRoot();
			} else {
				return this;
			}
		}

		public <R> TaskNode<R> createTaskNode() {
			TaskNode<R> child = new TaskNode<>(this);
			this.allChildren.add(child);
			this.unjoinedChildren.add(child);
			return child;
		}

		@Override
		public <R> Future<R> submitCallable(ExecutorService executorService, CheckedCallable<R> body) {
			TaskNode<R> child = createTaskNode();
			child.future = executorService.submit(() -> {
				Deque<Context> stack = contextStack.get();
				stack.push(child);
				try {
					return body.call();
				} finally {
					stack.pop();
				}
			});
			return child.future;
		}

		@Override
		public Future<Void> submitRunnable(ExecutorService executorService, CheckedRunnable body) {
			TaskNode<Void> child = createTaskNode();
			child.future = executorService.submit(() -> {
				Deque<Context> stack = contextStack.get();
				stack.push(child);
				try {
					body.run();
				} finally {
					stack.pop();
				}
				return null;
			});
			return child.future;
		}

		public void doWork(long n) {
			work.addAndGet(n);
		}

		public long totalWork() {
			long result = work.get();
			for (Node child : allChildren) {
				result += child.totalWork();
			}
			return result;
		}

		protected void join() throws InterruptedException, ExecutionException {
			while (unjoinedChildren.size() > 0) {
				Node child = unjoinedChildren.poll();
				child.join();
			}
		}
	}

	private static class FinishNode extends Node implements FinishContext {
		private final FinishAccumulator<?>[] accumulators;

		public FinishNode(Node parent, FinishAccumulator<?>[] accumulators) {
			super(parent);
			this.accumulators = accumulators;
			if (this.accumulators != null) {
				for (FinishAccumulator<?> a : this.accumulators) {
					Accumulator<?> acc = (Accumulator<?>) a;
					acc.open();
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
		public void joinAllTasks() throws InterruptedException, ExecutionException {
			this.join();
		}
	}

	private static class TaskNode<R> extends Node {
		private volatile Future<R> future;

		public TaskNode(Node parent) {
			super(parent);
		}

		@Override
		protected void join() throws InterruptedException, ExecutionException {
			while (future == null) {
				// Thread.yield();
			}
			this.future.get();
			super.join();
		}
	}
}

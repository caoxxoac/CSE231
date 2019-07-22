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
package edu.wustl.cse231s.v5;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;

import edu.wustl.cse231s.v5.api.AccumulatorReducer;
import edu.wustl.cse231s.v5.api.Activity;
import edu.wustl.cse231s.v5.api.Bookkeeping;
import edu.wustl.cse231s.v5.api.CheckedCallable;
import edu.wustl.cse231s.v5.api.CheckedConsumer;
import edu.wustl.cse231s.v5.api.CheckedIntConsumer;
import edu.wustl.cse231s.v5.api.CheckedIntIntConsumer;
import edu.wustl.cse231s.v5.api.CheckedRunnable;
import edu.wustl.cse231s.v5.api.ContentionLevel;
import edu.wustl.cse231s.v5.api.DoubleAccumulationDeterminismPolicy;
import edu.wustl.cse231s.v5.api.FinishAccumulator;
import edu.wustl.cse231s.v5.api.Metrics;
import edu.wustl.cse231s.v5.api.NumberReductionOperator;
import edu.wustl.cse231s.v5.impl.V5Impl;
import edu.wustl.cse231s.v5.impl.executor.BookkeepingExecutorXV5Impl;
import edu.wustl.cse231s.v5.impl.executor.ExecutorV5Impl;
import edu.wustl.cse231s.v5.options.AwaitFuturesOption;
import edu.wustl.cse231s.v5.options.ChunkedOption;
import edu.wustl.cse231s.v5.options.RegisterAccumulatorsOption;
import edu.wustl.cse231s.v5.options.SingleOption;
import edu.wustl.cse231s.v5.options.SystemPropertiesOption;
import edu.wustl.cse231s.v5.serial.SerialV5Impl;

/**
 * @see <a href="https://dl.acm.org/citation.cfm?doid=1094811.1094852">X10: An
 *      Object-Oriented Approach to Non-Uniform Cluster Computing</a>
 * 
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class V5 {
	private static AtomicReference<V5Impl> implAtom = new AtomicReference<V5Impl>(null);

	private static V5Impl getImpl() {
		V5Impl impl = implAtom.get();
		if (impl != null) {
			return impl;
		} else {
			throw new IllegalStateException("launchApp() required");
		}
	}

	/**
	 * Executes the specified computation, waiting for all spawned async and future
	 * descendant tasks to terminate.
	 * 
	 * @param body
	 *            run method defines the computation
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	public static void finish(CheckedRunnable body) throws InterruptedException, ExecutionException {
		getImpl().finish(body);
	}

	/**
	 * Spawn an asynchronous child task.
	 * 
	 * @param body
	 *            run method defines the computation of the task
	 */
	public static void async(CheckedRunnable body) {
		getImpl().async(body);
	}

	/**
	 * Conditionally spawn an asynchronous child task.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>async(rangeLength&gt;threshold, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>if (rangeLength&gt;threshold) {
	 *     async(body);
	 * } else {
	 *     body.run();
	 * }</code>
	 * </pre>
	 * 
	 * @param isParallelDesired
	 *            whether or not to run the task in parallel
	 * @param body
	 *            run method defines the execution of the task
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	public static void async(boolean isParallelDesired, CheckedRunnable body)
			throws InterruptedException, ExecutionException {
		if (isParallelDesired) {
			async(body);
		} else {
			body.run();
		}
	}

	/**
	 * Spawn an asynchronous child task which returns a value.
	 * 
	 * @param <R>
	 *            the return type of the computation
	 * @param body
	 *            run method defines the computation of the task
	 * @return a Future object from which the result of the computation can be
	 *         acquired via its get() method.
	 */
	public static <R> Future<R> future(CheckedCallable<R> body) {
		return getImpl().future(body);
	}

	/**
	 * Conditionally spawn an asynchronous child task.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>return future(rangeLength&gt;threshold, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>if (rangeLength&gt;threshold) {
	 *     return future(body);
	 * } else {
	 *     R value = body.call();
	 *     return ConcurrentUtils.constantFuture(value);
	 * }</code>
	 * </pre>
	 * 
	 * @param <R>
	 *            the return type of the computation
	 * @param isParallelDesired
	 *            whether or not to run the task in parallel
	 * @param body
	 *            call method defines the execution of the task
	 * @return a Future object from which the result of the computation can be
	 *         acquired via its get() method.
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	public static <R> Future<R> future(boolean isParallelDesired, CheckedCallable<R> body)
			throws InterruptedException, ExecutionException {
		if (isParallelDesired) {
			return future(body);
		} else {
			R value = body.call();
			return ConcurrentUtils.constantFuture(value);
		}
	}

	/**
	 * Sequential loop over the range [min, maxExclusive).
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forseq(0, N, (int i) -&gt; {
	 *     f(i);
	 * });</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>for(int i=0; i&lt;N; i++) {
	 *     f(i);
	 * }</code>
	 * </pre>
	 * 
	 * @param min
	 *            minimum (inclusive) index of the for loop
	 * @param maxExclusive
	 *            maximum (exclusive) index of the for loop
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	public static void forseq(int min, int maxExclusive, CheckedIntConsumer body)
			throws InterruptedException, ExecutionException {
		getImpl().forseq(min, maxExclusive, body);
	}

	/**
	 * Parallel loop over the range [min, maxExclusive) without a wrapping finish.
	 * 
	 * Although the implementation may divide-and-conquer the range, the semantics
	 * of:
	 * 
	 * <pre>
	 * <code>forasync(0, N, (int i) -&gt; {
	 *     f(i);
	 * });</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>for(int i=0; i&lt;N; i++) {
	 *     async(() -&gt; {
	 *         f(i);
	 *     });
	 * }</code>
	 * </pre>
	 * 
	 * @param min
	 *            minimum (inclusive) index of the parallel for loop
	 * @param maxExclusive
	 *            maximum (exclusive) index of the parallel for loop
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	public static void forasync(int min, int maxExclusive, CheckedIntConsumer body)
			throws InterruptedException, ExecutionException {
		getImpl().forasync(min, maxExclusive, body);
	}

	/**
	 * Parallel loop over the range [min, maxExclusive) with a wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forall(0, N, (int i) -&gt; {
	 *     f(i);
	 * });</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>finish(() -&gt; {
	 *     forasync(0, N, (int i) -&gt; {
	 *         f(i);
	 *     });
	 * });</code>
	 * </pre>
	 * 
	 * @param min
	 *            minimum (inclusive) index of the parallel for loop
	 * @param maxExclusive
	 *            maximum (exclusive) index of the parallel for loop
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #finish(CheckedRunnable)
	 * @see #forasync(int, int, CheckedIntConsumer)
	 */
	public static void forall(int min, int maxExclusive, CheckedIntConsumer body)
			throws InterruptedException, ExecutionException {
		getImpl().forall(min, maxExclusive, body);
	}

	/**
	 * Conditionally parallel loop over the range [min, maxExclusive) without a
	 * wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forasync(isParallelDesired, min, maxExclusive, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>if (isParallelDesired) {
	 *     forasync(min, maxExclusive, body);
	 * } else {
	 *     forseq(min, maxExclusive, body);
	 * }</code>
	 * </pre>
	 * 
	 * @param isParallelDesired
	 *            whether or not to run the tasks in parallel
	 * @param min
	 *            minimum (inclusive) index of the parallel for loop
	 * @param maxExclusive
	 *            maximum (exclusive) index of the parallel for loop
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #forasync(int, int, CheckedIntConsumer)
	 * @see #forseq(int, int, CheckedIntConsumer)
	 */
	public static void forasync(boolean isParallelDesired, int min, int maxExclusive, CheckedIntConsumer body)
			throws InterruptedException, ExecutionException {
		if (isParallelDesired) {
			forasync(min, maxExclusive, body);
		} else {
			forseq(min, maxExclusive, body);
		}
	}

	/**
	 * Conditionally parallel loop over the range [min, maxExclusive) with a
	 * wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forall(isParallelDesired, min, maxExclusive, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>if (isParallelDesired) {
	 *     forall(min, maxExclusive, body);
	 * } else {
	 *     forseq(min, maxExclusive, body);
	 * }</code>
	 * </pre>
	 * 
	 * @param isParallelDesired
	 *            whether or not to run the tasks in parallel
	 * @param min
	 *            minimum (inclusive) index of the parallel for loop
	 * @param maxExclusive
	 *            maximum (exclusive) index of the parallel for loop
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #forall(int, int, CheckedIntConsumer)
	 * @see #forseq(int, int, CheckedIntConsumer)
	 */
	public static void forall(boolean isParallelDesired, int min, int maxExclusive, CheckedIntConsumer body)
			throws InterruptedException, ExecutionException {
		if (isParallelDesired) {
			forall(min, maxExclusive, body);
		} else {
			forseq(min, maxExclusive, body);
		}
	}

	/**
	 * Sequential loop over the range [min, maxExclusive) with a chunked() option to
	 * allow easy switching back and forth between parallel and sequential code
	 * versions.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forseq(chunked(), min, maxExclusive, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>forseq(min, maxExclusive, body);</code>
	 * </pre>
	 * 
	 * @param chunkedOption
	 *            result of either {@link #chunked()} or {@link #chunked(int)}
	 * @param min
	 *            minimum (inclusive) index of the for loop
	 * @param maxExclusive
	 *            maximum (exclusive) index of the for loop
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #chunked()
	 * @see #chunked(int)
	 * @see #forseq(int, int, CheckedIntConsumer)
	 */
	public static void forseq(ChunkedOption chunkedOption, int min, int maxExclusive, CheckedIntConsumer body)
			throws InterruptedException, ExecutionException {
		getImpl().forseq(chunkedOption, min, maxExclusive, body);
	}

	/**
	 * Chunked parallel loop over the range [min, maxExclusive) without a wrapping
	 * finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forasync(chunked(), min, maxExclusive, body);</code>
	 * </pre>
	 * 
	 * is similar to {@link #forasync(int, int, CheckedIntConsumer)} except that the
	 * runtime will stop short of creating a task per index when the range falls
	 * below the desired the chunk size.
	 * 
	 * @param chunkedOption
	 *            result of either {@link #chunked()} or {@link #chunked(int)}
	 * @param min
	 *            minimum (inclusive) index of the parallel for loop
	 * @param maxExclusive
	 *            maximum (exclusive) index of the parallel for loop
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #chunked()
	 * @see #chunked(int)
	 */
	public static void forasync(ChunkedOption chunkedOption, int min, int maxExclusive, CheckedIntConsumer body)
			throws InterruptedException, ExecutionException {
		getImpl().forasync(chunkedOption, min, maxExclusive, body);
	}

	/**
	 * Chunked parallel loop over the range [min, maxExclusive) with a wrapping
	 * finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forall(chunked(), 0, N, (int i) -&gt; {
	 *     f(i);
	 * });</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>finish(() -&gt; {
	 *     forasync(chunked(), 0, N, (int i) -&gt; {
	 *         f(i);
	 *     });
	 * });</code>
	 * </pre>
	 * 
	 * @param chunkedOption
	 *            result of either {@link #chunked()} or {@link #chunked(int)}
	 * @param min
	 *            minimum (inclusive) index of the parallel for loop
	 * @param maxExclusive
	 *            maximum (exclusive) index of the parallel for loop
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #finish(CheckedRunnable)
	 * @see #forasync(ChunkedOption, int, int, CheckedIntConsumer)
	 */
	public static void forall(ChunkedOption chunkedOption, int min, int maxExclusive, CheckedIntConsumer body)
			throws InterruptedException, ExecutionException {
		getImpl().forall(chunkedOption, min, maxExclusive, body);
	}

	/**
	 * Chunked conditionally parallel loop over the range [min, maxExclusive)
	 * without a wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forasync(isParallelDesired, chunked(), min, maxExclusive, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>if (isParallelDesired) {
	 *     forasync(chunked(), min, maxExclusive, body);
	 * } else {
	 *     forseq(min, maxExclusive, body);
	 * }</code>
	 * </pre>
	 * 
	 * @param isParallelDesired
	 *            whether or not to run the tasks in parallel
	 * @param chunkedOption
	 *            result of either {@link #chunked()} or {@link #chunked(int)}
	 * @param min
	 *            minimum (inclusive) index of the parallel for loop
	 * @param maxExclusive
	 *            maximum (exclusive) index of the parallel for loop
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #forasync(ChunkedOption, int, int, CheckedIntConsumer)
	 * @see #forseq(int, int, CheckedIntConsumer)
	 */
	public static void forasync(boolean isParallelDesired, ChunkedOption chunkedOption, int min, int maxExclusive,
			CheckedIntConsumer body) throws InterruptedException, ExecutionException {
		if (isParallelDesired) {
			forasync(chunkedOption, min, maxExclusive, body);
		} else {
			forseq(min, maxExclusive, body);
		}
	}

	/**
	 * Chunked conditionally parallel loop over the range [min, maxExclusive) with a
	 * wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forall(isParallelDesired, chunked(), min, maxExclusive, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>if (isParallelDesired) {
	 *     forall(min, chunked(), maxExclusive, body);
	 * } else {
	 *     forseq(min, maxExclusive, body);
	 * }</code>
	 * </pre>
	 * 
	 * @param isParallelDesired
	 *            whether or not to run the tasks in parallel
	 * @param chunkedOption
	 *            result of either {@link #chunked()} or {@link #chunked(int)}
	 * @param min
	 *            minimum (inclusive) index of the parallel for loop
	 * @param maxExclusive
	 *            maximum (exclusive) index of the parallel for loop
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #forall(ChunkedOption, int, int, CheckedIntConsumer)
	 * @see #forseq(int, int, CheckedIntConsumer)
	 */
	public static void forall(boolean isParallelDesired, ChunkedOption chunkedOption, int min, int maxExclusive,
			CheckedIntConsumer body) throws InterruptedException, ExecutionException {
		if (isParallelDesired) {
			forall(chunkedOption, min, maxExclusive, body);
		} else {
			forseq(min, maxExclusive, body);
		}
	}

	/**
	 * Sequential loop over an array.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forseq(array, (T element) -&gt; {
	 *     f(element);
	 * });</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>
	 * for(T element : array) {
	 *     f(element);
	 * }
	 * </code>
	 * </pre>
	 * 
	 * @param <T>
	 *            the component type of the array
	 * @param array
	 *            the array which the loop operates on
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	public static <T> void forseq(T[] array, CheckedConsumer<T> body) throws InterruptedException, ExecutionException {
		getImpl().forseq(array, body);
	}

	/**
	 * Parallel loop over an array without a wrapping finish.
	 * 
	 * Although the implementation may divide-and-conquer the array, the semantics
	 * of:
	 * 
	 * <pre>
	 * <code>forasync(array, (T element) -&gt; {
	 *     f(element);
	 * });</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>for(T element : array) {
	 *     async(() -&gt; {
	 *         f(element);
	 *     });
	 * }</code>
	 * </pre>
	 * 
	 * @param <T>
	 *            the component type of the array
	 * @param array
	 *            the array which the loop operates on
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	public static <T> void forasync(T[] array, CheckedConsumer<T> body)
			throws InterruptedException, ExecutionException {
		getImpl().forasync(array, body);
	}

	/**
	 * Parallel loop over an array with a wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forall(array, (T element) -&gt; {
	 *     f(element);
	 * });</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>finish(() -&gt; {
	 *     forasync(array, (T element) -&gt; {
	 *         f(i);
	 *     });
	 * });</code>
	 * </pre>
	 * 
	 * @param <T>
	 *            the component type of the array
	 * @param array
	 *            the array which the loop operates on
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #finish(CheckedRunnable)
	 * @see #forasync(Object[], CheckedConsumer)
	 */
	public static <T> void forall(T[] array, CheckedConsumer<T> body) throws InterruptedException, ExecutionException {
		getImpl().forall(array, body);
	}

	/**
	 * Conditionally parallel loop over an array without a wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forasync(isParallelDesired, array, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>if (isParallelDesired) {
	 *     forasync(array, body);
	 * } else {
	 *     forseq(array, body);
	 * }</code>
	 * </pre>
	 * 
	 * @param <T>
	 *            the component type of the array
	 * @param isParallelDesired
	 *            whether or not to run the tasks in parallel
	 * @param array
	 *            the array which the loop operates on
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #forasync(Object[], CheckedConsumer)
	 * @see #forseq(Object[], CheckedConsumer)
	 */
	public static <T> void forasync(boolean isParallelDesired, T[] array, CheckedConsumer<T> body)
			throws InterruptedException, ExecutionException {
		if (isParallelDesired) {
			forasync(array, body);
		} else {
			forseq(array, body);
		}
	}

	/**
	 * Conditionally parallel loop over an array with a wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forall(isParallelDesired, array, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>if (isParallelDesired) {
	 *     forall(array, body);
	 * } else {
	 *     forseq(array, body);
	 * }</code>
	 * </pre>
	 * 
	 * @param <T>
	 *            the component type of the array
	 * @param isParallelDesired
	 *            whether or not to run the tasks in parallel
	 * @param array
	 *            the array which the loop operates on
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #forall(Object[], CheckedConsumer)
	 * @see #forseq(Object[], CheckedConsumer)
	 */
	public static <T> void forall(boolean isParallelDesired, T[] array, CheckedConsumer<T> body)
			throws InterruptedException, ExecutionException {
		if (isParallelDesired) {
			forall(array, body);
		} else {
			forseq(array, body);
		}
	}

	/**
	 * Sequential loop over an array with a chunked() option to allow easy switching
	 * back and forth between parallel and sequential code versions.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forseq(chunked(), array, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>forseq(array, body);</code>
	 * </pre>
	 * 
	 * @param <T>
	 *            the component type of the array
	 * @param chunkedOption
	 *            result of either {@link #chunked()} or {@link #chunked(int)}
	 * @param array
	 *            the array which the loop operates on
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #chunked()
	 * @see #chunked(int)
	 * @see #forseq(Object[], CheckedConsumer)
	 */
	public static <T> void forseq(ChunkedOption chunkedOption, T[] array, CheckedConsumer<T> body)
			throws InterruptedException, ExecutionException {
		getImpl().forseq(chunkedOption, array, body);
	}

	/**
	 * Chunked parallel loop over an array without a wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forasync(chunked(), array, body);</code>
	 * </pre>
	 * 
	 * is similar to {@link #forasync(Object[], CheckedConsumer)} except that the
	 * runtime will stop short of creating a task per element when the range falls
	 * below the desired the chunk size.
	 * 
	 * @param <T>
	 *            the component type of the array
	 * @param chunkedOption
	 *            result of either {@link #chunked()} or {@link #chunked(int)}
	 * @param array
	 *            the array which the loop operates on
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #chunked()
	 * @see #chunked(int)
	 */
	public static <T> void forasync(ChunkedOption chunkedOption, T[] array, CheckedConsumer<T> body)
			throws InterruptedException, ExecutionException {
		getImpl().forasync(chunkedOption, array, body);
	}

	/**
	 * Chunked parallel loop over an array with a wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forall(chunked(), array, (T element) -&gt; {
	 *     f(element);
	 * });</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>finish(() -&gt; {
	 *     forasync(chunked(), array, (T element) -&gt; {
	 *         f(element);
	 *     });
	 * });</code>
	 * </pre>
	 * 
	 * @param <T>
	 *            the component type of the array
	 * @param chunkedOption
	 *            result of either {@link #chunked()} or {@link #chunked(int)}
	 * @param array
	 *            the array which the loop operates on
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #finish(CheckedRunnable)
	 * @see #forasync(ChunkedOption, Object[], CheckedConsumer)
	 */
	public static <T> void forall(ChunkedOption chunkedOption, T[] array, CheckedConsumer<T> body)
			throws InterruptedException, ExecutionException {
		getImpl().forall(chunkedOption, array, body);
	}

	/**
	 * Chunked conditionally parallel loop over an array without a wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forasync(isParallelDesired, chunked(), array, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>if (isParallelDesired) {
	 *     forasync(chunked(), array, body);
	 * } else {
	 *     forseq(array, body);
	 * }</code>
	 * </pre>
	 * 
	 * @param <T>
	 *            the component type of the array
	 * @param isParallelDesired
	 *            whether or not to run the tasks in parallel
	 * @param chunkedOption
	 *            result of either {@link #chunked()} or {@link #chunked(int)}
	 * @param array
	 *            the array which the loop operates on
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #forasync(ChunkedOption, Object[], CheckedConsumer)
	 * @see #forseq(Object[], CheckedConsumer)
	 */
	public static <T> void forasync(boolean isParallelDesired, ChunkedOption chunkedOption, T[] array,
			CheckedConsumer<T> body) throws InterruptedException, ExecutionException {
		if (isParallelDesired) {
			forasync(chunkedOption, array, body);
		} else {
			forseq(array, body);
		}
	}

	/**
	 * Chunked conditionally parallel loop over an array with a wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forall(isParallelDesired, chunked(), array, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>if (isParallelDesired) {
	 *     forall(chunked(), array, body);
	 * } else {
	 *     forseq(array, body);
	 * }</code>
	 * </pre>
	 * 
	 * @param <T>
	 *            the component type of the array
	 * @param isParallelDesired
	 *            whether or not to run the tasks in parallel
	 * @param chunkedOption
	 *            result of either {@link #chunked()} or {@link #chunked(int)}
	 * @param array
	 *            the array which the loop operates on
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #forall(ChunkedOption, Object[], CheckedConsumer)
	 * @see #forseq(Object[], CheckedConsumer)
	 */
	public static <T> void forall(boolean isParallelDesired, ChunkedOption chunkedOption, T[] array,
			CheckedConsumer<T> body) throws InterruptedException, ExecutionException {
		if (isParallelDesired) {
			forall(chunkedOption, array, body);
		} else {
			forseq(array, body);
		}
	}

	/**
	 * Sequential loop over an iterable.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forseq(iterable, (T element) -&gt; {
	 *     f(element);
	 * });</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>
	 * for(T item : iterable) {
	 *     f(item);
	 * }
	 * </code>
	 * </pre>
	 * 
	 * @param <T>
	 *            the parameterized type of the iterable
	 * @param iterable
	 *            the iterable which the loop operates on
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	public static <T> void forseq(Iterable<T> iterable, CheckedConsumer<T> body)
			throws InterruptedException, ExecutionException {
		getImpl().forseq(iterable, body);
	}

	/**
	 * Although the implementation may divide-and-conquer the iterable, the
	 * semantics of:
	 * 
	 * <pre>
	 * <code>forasync(iterable, (T item) -&gt; {
	 *     f(item);
	 * });</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>for(T item : iterable) {
	 *     async(() -&gt; {
	 *         f(item);
	 *     });
	 * }</code>
	 * </pre>
	 * 
	 * @param <T>
	 *            the parameterized type of the iterable
	 * @param iterable
	 *            the iterable which the loop operates on
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	public static <T> void forasync(Iterable<T> iterable, CheckedConsumer<T> body)
			throws InterruptedException, ExecutionException {
		getImpl().forasync(iterable, body);
	}

	/**
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forall(iterable, (T element) -&gt; {
	 *     f(element);
	 * });</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>finish(() -&gt; {
	 *     forasync(iterable, (T element) -&gt; {
	 *         f(i);
	 *     });
	 * });</code>
	 * </pre>
	 * 
	 * @param <T>
	 *            the parameterized type of the iterable
	 * @param iterable
	 *            the iterable which the loop operates on
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #finish(CheckedRunnable)
	 * @see #forasync(Iterable, CheckedConsumer)
	 */
	public static <T> void forall(Iterable<T> iterable, CheckedConsumer<T> body)
			throws InterruptedException, ExecutionException {
		getImpl().forall(iterable, body);
	}

	/**
	 * Parallel loop over an iterable without a wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forasync(isParallelDesired, iterable, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>if (isParallelDesired) {
	 *     forasync(iterable, body);
	 * } else {
	 *     forseq(iterable, body);
	 * }</code>
	 * </pre>
	 * 
	 * @param <T>
	 *            the parameterized type of the iterable
	 * @param isParallelDesired
	 *            whether or not to run the tasks in parallel
	 * @param iterable
	 *            the iterable which the loop operates on
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #forasync(Iterable, CheckedConsumer)
	 * @see #forseq(Iterable, CheckedConsumer)
	 */
	public static <T> void forasync(boolean isParallelDesired, Iterable<T> iterable, CheckedConsumer<T> body)
			throws InterruptedException, ExecutionException {
		if (isParallelDesired) {
			forasync(iterable, body);
		} else {
			forseq(iterable, body);
		}
	}

	/**
	 * Parallel loop over an iterable with a wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forall(isParallelDesired, iterable, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>if (isParallelDesired) {
	 *     forall(iterable, body);
	 * } else {
	 *     forseq(iterable, body);
	 * }</code>
	 * </pre>
	 * 
	 * @param <T>
	 *            the parameterized type of the iterable
	 * @param isParallelDesired
	 *            whether or not to run the tasks in parallel
	 * @param iterable
	 *            the iterable which the loop operates on
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #forall(Iterable, CheckedConsumer)
	 * @see #forseq(Iterable, CheckedConsumer)
	 */
	public static <T> void forall(boolean isParallelDesired, Iterable<T> iterable, CheckedConsumer<T> body)
			throws InterruptedException, ExecutionException {
		if (isParallelDesired) {
			forall(iterable, body);
		} else {
			forseq(iterable, body);
		}
	}

	/**
	 * Sequential loop over an array with a chunked() option to allow easy switching
	 * back and forth between parallel and sequential code versions.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forseq(chunked(), iterable, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>forseq(iterable, body);</code>
	 * </pre>
	 * 
	 * @param <T>
	 *            the parameterized type of the iterable
	 * @param chunkedOption
	 *            result of either {@link #chunked()} or {@link #chunked(int)}
	 * @param iterable
	 *            the iterable which the loop operates on
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #chunked()
	 * @see #chunked(int)
	 * @see #forseq(Object[], CheckedConsumer)
	 */
	public static <T> void forseq(ChunkedOption chunkedOption, Iterable<T> iterable, CheckedConsumer<T> body)
			throws InterruptedException, ExecutionException {
		getImpl().forseq(chunkedOption, iterable, body);
	}

	/**
	 * Chunked parallel loop over an iterable without a wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forasync(chunked(), iterable, body);</code>
	 * </pre>
	 * 
	 * is similar to {@link #forasync(Iterable, CheckedConsumer)} except that the
	 * runtime will stop short of creating a task per item when the range falls
	 * below the desired the chunk size.
	 * 
	 * @param <T>
	 *            the parameterized type of the iterable
	 * @param chunkedOption
	 *            result of either {@link #chunked()} or {@link #chunked(int)}
	 * @param iterable
	 *            the iterable which the loop operates on
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #chunked()
	 * @see #chunked(int)
	 */
	public static <T> void forasync(ChunkedOption chunkedOption, Iterable<T> iterable, CheckedConsumer<T> body)
			throws InterruptedException, ExecutionException {
		getImpl().forasync(chunkedOption, iterable, body);
	}

	/**
	 * Chunked parallel loop over an iterable with a wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forall(chunked(), iterable, (T item) -&gt; {
	 *     f(element);
	 * });</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>finish(() -&gt; {
	 *     forasync(chunked(), iterable, (T item) -&gt; {
	 *         f(item);
	 *     });
	 * });</code>
	 * </pre>
	 * 
	 * @param <T>
	 *            the parameterized type of the iterable
	 * @param chunkedOption
	 *            result of either {@link #chunked()} or {@link #chunked(int)}
	 * @param iterable
	 *            the iterable which the loop operates on
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #finish(CheckedRunnable)
	 * @see #forasync(ChunkedOption, Iterable, CheckedConsumer)
	 */
	public static <T> void forall(ChunkedOption chunkedOption, Iterable<T> iterable, CheckedConsumer<T> body)
			throws InterruptedException, ExecutionException {
		getImpl().forall(chunkedOption, iterable, body);
	}

	/**
	 * Chunked conditionally parallel loop over an iterable without a wrapping
	 * finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forasync(isParallelDesired, chunked(), iterable, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>if (isParallelDesired) {
	 *     forasync(chunked(), iterable, body);
	 * } else {
	 *     forseq(iterable, body);
	 * }</code>
	 * </pre>
	 * 
	 * @param <T>
	 *            the parameterized type of the iterable
	 * @param isParallelDesired
	 *            whether or not to run the tasks in parallel
	 * @param chunkedOption
	 *            result of either {@link #chunked()} or {@link #chunked(int)}
	 * @param iterable
	 *            the iterable which the loop operates on
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #forasync(ChunkedOption, Iterable, CheckedConsumer)
	 * @see #forseq(Iterable, CheckedConsumer)
	 */
	public static <T> void forasync(boolean isParallelDesired, ChunkedOption chunkedOption, Iterable<T> iterable,
			CheckedConsumer<T> body) throws InterruptedException, ExecutionException {
		if (isParallelDesired) {
			forasync(chunkedOption, iterable, body);
		} else {
			forseq(iterable, body);
		}
	}

	/**
	 * Chunked conditionally parallel loop over an iterable with a wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forall(isParallelDesired, chunked(), iterable, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>if (isParallelDesired) {
	 *     forall(chunked(), iterable, body);
	 * } else {
	 *     forseq(iterable, body);
	 * }</code>
	 * </pre>
	 * 
	 * @param <T>
	 *            the parameterized type of the iterable
	 * @param isParallelDesired
	 *            whether or not to run the tasks in parallel
	 * @param chunkedOption
	 *            result of either {@link #chunked()} or {@link #chunked(int)}
	 * @param iterable
	 *            the iterable which the loop operates on
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #forall(ChunkedOption, Iterable, CheckedConsumer)
	 * @see #forseq(Iterable, CheckedConsumer)
	 */
	public static <T> void forall(boolean isParallelDesired, ChunkedOption chunkedOption, Iterable<T> iterable,
			CheckedConsumer<T> body) throws InterruptedException, ExecutionException {
		if (isParallelDesired) {
			forall(chunkedOption, iterable, body);
		} else {
			forseq(iterable, body);
		}
	}

	/**
	 * Nested sequential loops over the ranges [minA, maxExclusiveA) and [minB,
	 * maxExclusiveB).
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forseq2d(minA, maxExclusiveA, minB, maxExclusiveB, (int i, int j) -&gt; {
	 *     f(i, j);
	 * });</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>for(int i=minA; i&lt;maxExclusiveA; i++) {
	 *     for(int j=minB; j&lt;maxExclusiveB; j++) {
	 *         f(i, j);
	 *     }
	 * }</code>
	 * </pre>
	 * 
	 * @param minA
	 *            minimum (inclusive) index of the outer loop
	 * @param maxExclusiveA
	 *            maximum (exclusive) index of the outer loop
	 * @param minB
	 *            minimum (inclusive) index of the inner loop
	 * @param maxExclusiveB
	 *            maximum (exclusive) index of the inner loop
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	public static void forseq2d(int minA, int maxExclusiveA, int minB, int maxExclusiveB, CheckedIntIntConsumer body)
			throws InterruptedException, ExecutionException {
		getImpl().forseq2d(minA, maxExclusiveA, minB, maxExclusiveB, body);
	}

	/**
	 * Parallel loop over the nested ranges [minA, maxExclusiveA) and [minB,
	 * maxExclusiveB) without a wrapping finish.
	 * 
	 * Although the implementation may divide-and-conquer the range(s), the
	 * semantics of:
	 * 
	 * <pre>
	 * <code>forasync2d(minA, maxExclusiveA, minB, maxExclusiveB, (int i, int j) -&gt; {
	 *     f(i, j);
	 * });</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>for(int i=minA; i&lt;maxExclusiveA; i++) {
	 *     for(int j=minB; j&lt;maxExclusiveB; j++) {
	 *         async(() -&gt; {
	 *             f(i, j);
	 *         });
	 *     }
	 * }</code>
	 * </pre>
	 * 
	 * @param minA
	 *            minimum (inclusive) index of the outer loop
	 * @param maxExclusiveA
	 *            maximum (exclusive) index of the outer loop
	 * @param minB
	 *            minimum (inclusive) index of the inner loop
	 * @param maxExclusiveB
	 *            maximum (exclusive) index of the inner loop
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	public static void forasync2d(int minA, int maxExclusiveA, int minB, int maxExclusiveB, CheckedIntIntConsumer body)
			throws InterruptedException, ExecutionException {
		getImpl().forasync2d(minA, maxExclusiveA, minB, maxExclusiveB, body);
	}

	/**
	 * Parallel loop over the nested ranges [minA, maxExclusiveA) and [minB,
	 * maxExclusiveB) with a wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forall2d(minA, maxExclusiveA, minB, maxExclusiveB, (int i, int j) -&gt; {
	 *     f(i, j);
	 * });</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>finish(() -&gt; {
	 *     forasync2d(minA, maxExclusiveA, minB, maxExclusiveB, (int i, int j) -&gt; {
	 *         f(i, j);
	 *     });
	 * });</code>
	 * </pre>
	 * 
	 * @param minA
	 *            minimum (inclusive) index of the outer loop
	 * @param maxExclusiveA
	 *            maximum (exclusive) index of the outer loop
	 * @param minB
	 *            minimum (inclusive) index of the inner loop
	 * @param maxExclusiveB
	 *            maximum (exclusive) index of the inner loop
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #finish(CheckedRunnable)
	 * @see #forasync2d(int, int, int, int, CheckedIntIntConsumer)
	 */
	public static void forall2d(int minA, int maxExclusiveA, int minB, int maxExclusiveB, CheckedIntIntConsumer body)
			throws InterruptedException, ExecutionException {
		getImpl().forall2d(minA, maxExclusiveA, minB, maxExclusiveB, body);
	}

	/**
	 * Conditionally parallel loop over the nested ranges [minA, maxExclusiveA) and
	 * [minB, maxExclusiveB) without a wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forasync2d(isParallelDesired, minA, maxExclusiveA, minB, maxExclusiveB, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>if (isParallelDesired) {
	 *     forasync2d(minA, maxExclusiveA, minB, maxExclusiveB, body);
	 * } else {
	 *     forseq2d(minA, maxExclusiveA, minB, maxExclusiveB, body);
	 * }</code>
	 * </pre>
	 * 
	 * @param isParallelDesired
	 *            whether or not to run the tasks in parallel
	 * @param minA
	 *            minimum (inclusive) index of the outer loop
	 * @param maxExclusiveA
	 *            maximum (exclusive) index of the outer loop
	 * @param minB
	 *            minimum (inclusive) index of the inner loop
	 * @param maxExclusiveB
	 *            maximum (exclusive) index of the inner loop
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #forasync2d(int, int, int, int, CheckedIntIntConsumer)
	 * @see #forseq2d(int, int, int, int, CheckedIntIntConsumer)
	 */
	public static void forasync2d(boolean isParallelDesired, int minA, int maxExclusiveA, int minB, int maxExclusiveB,
			CheckedIntIntConsumer body) throws InterruptedException, ExecutionException {
		if (isParallelDesired) {
			forasync2d(minA, maxExclusiveA, minB, maxExclusiveB, body);
		} else {
			forseq2d(minA, maxExclusiveA, minB, maxExclusiveB, body);
		}
	}

	/**
	 * Conditionally parallel loop over the nested ranges [minA, maxExclusiveA) and
	 * [minB, maxExclusiveB) with a wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forall2d(isParallelDesired, minA, maxExclusiveA, minB, maxExclusiveB, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>if (isParallelDesired) {
	 *     forall2d(minA, maxExclusiveA, minB, maxExclusiveB, body);
	 * } else {
	 *     forseq2d(minA, maxExclusiveA, minB, maxExclusiveB, body);
	 * }</code>
	 * </pre>
	 * 
	 * @param isParallelDesired
	 *            whether or not to run the tasks in parallel
	 * @param minA
	 *            minimum (inclusive) index of the outer loop
	 * @param maxExclusiveA
	 *            maximum (exclusive) index of the outer loop
	 * @param minB
	 *            minimum (inclusive) index of the inner loop
	 * @param maxExclusiveB
	 *            maximum (exclusive) index of the inner loop
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #forall2d(int, int, int, int, CheckedIntIntConsumer)
	 * @see #forseq2d(int, int, int, int, CheckedIntIntConsumer)
	 */
	public static void forall2d(boolean isParallelDesired, int minA, int maxExclusiveA, int minB, int maxExclusiveB,
			CheckedIntIntConsumer body) throws InterruptedException, ExecutionException {
		if (isParallelDesired) {
			forall2d(minA, maxExclusiveA, minB, maxExclusiveB, body);
		} else {
			forseq2d(minA, maxExclusiveA, minB, maxExclusiveB, body);
		}
	}

	/**
	 * Nested sequential loops over the ranges [minA, maxExclusiveA) and [minB,
	 * maxExclusiveB) with a chunked() option to allow easy switching back and forth
	 * between parallel and sequential code versions.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forseq2d(chunked(), minA, maxExclusiveA, minB, maxExclusiveB, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>forseq2d(minA, maxExclusiveA, minB, maxExclusiveB, body);</code>
	 * </pre>
	 * 
	 * @param chunkedOption
	 *            result of either {@link #chunked()} or {@link #chunked(int)}
	 * @param minA
	 *            minimum (inclusive) index of the outer loop
	 * @param maxExclusiveA
	 *            maximum (exclusive) index of the outer loop
	 * @param minB
	 *            minimum (inclusive) index of the inner loop
	 * @param maxExclusiveB
	 *            maximum (exclusive) index of the inner loop
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * @see #chunked()
	 * @see #chunked(int)
	 * @see #forseq2d(int, int, int, int, CheckedIntIntConsumer)
	 */
	public static void forseq2d(ChunkedOption chunkedOption, int minA, int maxExclusiveA, int minB, int maxExclusiveB,
			CheckedIntIntConsumer body) throws InterruptedException, ExecutionException {
		getImpl().forseq2d(chunkedOption, minA, maxExclusiveA, minB, maxExclusiveB, body);
	}

	/**
	 * Chunked parallel loop over the nested ranges [minA, maxExclusiveA) and [minB,
	 * maxExclusiveB) without a wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forasync2d(chunked(), inA, maxExclusiveA, minB, maxExclusiveB, body);</code>
	 * </pre>
	 * 
	 * is similar to {@link #forasync2d(int, int, int, int, CheckedIntIntConsumer)}
	 * except that the runtime will stop short of creating a task per index when the
	 * range falls below the desired the chunk size.
	 * 
	 * @param chunkedOption
	 *            result of either {@link #chunked()} or {@link #chunked(int)}
	 * @param minA
	 *            minimum (inclusive) index of the outer loop
	 * @param maxExclusiveA
	 *            maximum (exclusive) index of the outer loop
	 * @param minB
	 *            minimum (inclusive) index of the inner loop
	 * @param maxExclusiveB
	 *            maximum (exclusive) index of the inner loop
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #chunked()
	 * @see #chunked(int)
	 */
	public static void forasync2d(ChunkedOption chunkedOption, int minA, int maxExclusiveA, int minB, int maxExclusiveB,
			CheckedIntIntConsumer body) throws InterruptedException, ExecutionException {
		getImpl().forasync2d(chunkedOption, minA, maxExclusiveA, minB, maxExclusiveB, body);
	}

	/**
	 * Chunked parallel loop over the nested ranges [minA, maxExclusiveA) and [minB,
	 * maxExclusiveB) with a wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forall(chunked(), minA, maxExclusiveA, minB, maxExclusiveB, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>finish(() -&gt; {
	 *     forasync(chunked(), minA, maxExclusiveA, minB, maxExclusiveB, body);
	 * });</code>
	 * </pre>
	 * 
	 * @param chunkedOption
	 *            result of either {@link #chunked()} or {@link #chunked(int)}
	 * @param minA
	 *            minimum (inclusive) index of the outer loop
	 * @param maxExclusiveA
	 *            maximum (exclusive) index of the outer loop
	 * @param minB
	 *            minimum (inclusive) index of the inner loop
	 * @param maxExclusiveB
	 *            maximum (exclusive) index of the inner loop
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #finish(CheckedRunnable)
	 * @see #forasync2d(ChunkedOption, int, int, int, int, CheckedIntIntConsumer)
	 */
	public static void forall2d(ChunkedOption chunkedOption, int minA, int maxExclusiveA, int minB, int maxExclusiveB,
			CheckedIntIntConsumer body) throws InterruptedException, ExecutionException {
		getImpl().forall2d(chunkedOption, minA, maxExclusiveA, minB, maxExclusiveB, body);
	}

	/**
	 * Conditionally chunked parallel loop over the nested ranges [minA,
	 * maxExclusiveA) and [minB, maxExclusiveB) without a wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forasync2d(isParallelDesired, chunked(), minA, maxExclusiveA, minB, maxExclusiveB, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>if (isParallelDesired) {
	 *     forasync2d(minA, chunked(), maxExclusiveA, minB, maxExclusiveB, body);
	 * } else {
	 *     forseq2d(minA, maxExclusiveA, minB, maxExclusiveB, body);
	 * }</code>
	 * </pre>
	 * 
	 * @param isParallelDesired
	 *            whether or not to run the tasks in parallel
	 * @param chunkedOption
	 *            result of either {@link #chunked()} or {@link #chunked(int)}
	 * @param minA
	 *            minimum (inclusive) index of the outer loop
	 * @param maxExclusiveA
	 *            maximum (exclusive) index of the outer loop
	 * @param minB
	 *            minimum (inclusive) index of the inner loop
	 * @param maxExclusiveB
	 *            maximum (exclusive) index of the inner loop
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #forasync2d(ChunkedOption, int, int, int, int, CheckedIntIntConsumer)
	 * @see #forseq2d(int, int, int, int, CheckedIntIntConsumer)
	 */
	public static void forasync2d(boolean isParallelDesired, ChunkedOption chunkedOption, int minA, int maxExclusiveA,
			int minB, int maxExclusiveB, CheckedIntIntConsumer body) throws InterruptedException, ExecutionException {
		if (isParallelDesired) {
			forasync2d(chunkedOption, minA, maxExclusiveA, minB, maxExclusiveB, body);
		} else {
			forseq2d(minA, maxExclusiveA, minB, maxExclusiveB, body);
		}
	}

	/**
	 * Conditionally chunked parallel loop over the nested ranges [minA,
	 * maxExclusiveA) and [minB, maxExclusiveB) with a wrapping finish.
	 * 
	 * The semantics of:
	 * 
	 * <pre>
	 * <code>forall2d(isParallelDesired, chunked(), minA, maxExclusiveA, minB, maxExclusiveB, body);</code>
	 * </pre>
	 * 
	 * is:
	 * 
	 * <pre>
	 * <code>if (isParallelDesired) {
	 *     forall2d(minA, chunked(), maxExclusiveA, minB, maxExclusiveB, body);
	 * } else {
	 *     forseq2d(minA, maxExclusiveA, minB, maxExclusiveB, body);
	 * }</code>
	 * </pre>
	 * 
	 * @param isParallelDesired
	 *            whether or not to run the tasks in parallel
	 * @param chunkedOption
	 *            result of either {@link #chunked()} or {@link #chunked(int)}
	 * @param minA
	 *            minimum (inclusive) index of the outer loop
	 * @param maxExclusiveA
	 *            maximum (exclusive) index of the outer loop
	 * @param minB
	 *            minimum (inclusive) index of the inner loop
	 * @param maxExclusiveB
	 *            maximum (exclusive) index of the inner loop
	 * @param body
	 *            accept method defines the execution of the tasks
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 * 
	 * @see #forall2d(ChunkedOption, int, int, int, int, CheckedIntIntConsumer)
	 * @see #forseq2d(int, int, int, int, CheckedIntIntConsumer)
	 */
	public static void forall2d(boolean isParallelDesired, ChunkedOption chunkedOption, int minA, int maxExclusiveA,
			int minB, int maxExclusiveB, CheckedIntIntConsumer body) throws InterruptedException, ExecutionException {
		if (isParallelDesired) {
			forall2d(chunkedOption, minA, maxExclusiveA, minB, maxExclusiveB, body);
		} else {
			forseq2d(minA, maxExclusiveA, minB, maxExclusiveB, body);
		}
	}

	public static void async(AwaitFuturesOption awaitFuturesOption, CheckedRunnable body) {
		getImpl().async(awaitFuturesOption, body);
	}

	public static <R> Future<R> future(AwaitFuturesOption awaitFuturesOption, CheckedCallable<R> body) {
		return getImpl().future(awaitFuturesOption, body);
	}

	public static void finish(RegisterAccumulatorsOption registerAccumulatorsOption, CheckedRunnable body)
			throws InterruptedException, ExecutionException {
		getImpl().finish(registerAccumulatorsOption, body);
	}

	public static int numWorkerThreads() {
		if (implAtom.get() != null) {
			return getImpl().numWorkerThreads();
		} else {
			// TODO
			// return Runtime.getRuntime().availableProcessors();
			throw new RuntimeException();
		}
	}

	public static boolean isLaunched() {
		return implAtom.get() != null;
	}

	public static void doWork(long n) {
		V5Impl impl = implAtom.get();
		if (impl != null) {
			impl.doWork(n);
		}
	}

	public static AwaitFuturesOption await(Future<?> futureA, Future<?>... futuresBtoZ) {
		return new AwaitFuturesOption(futureA, futuresBtoZ);
	}

	public static ChunkedOption chunked() {
		return new ChunkedOption();
	}

	public static ChunkedOption chunked(int size) {
		return new ChunkedOption(size);
	}

	public static RegisterAccumulatorsOption register(FinishAccumulator<?> accumulatorA,
			FinishAccumulator<?>... accumulatorBtoZ) {
		return new RegisterAccumulatorsOption(accumulatorA, accumulatorBtoZ);
	}

	public static SingleOption single(Runnable runnable) {
		return new SingleOption(runnable);
	}

	private static ContentionLevel getDefaultContentionLevel() {
		// TODO
		return ContentionLevel.LOW;
	}

	public static FinishAccumulator<Integer> newIntegerFinishAccumulator(NumberReductionOperator operator,
			ContentionLevel contentionLevel) {
		return getImpl().newIntegerFinishAccumulator(operator, contentionLevel);
	}

	public static FinishAccumulator<Integer> newIntegerFinishAccumulator(NumberReductionOperator operator) {
		return newIntegerFinishAccumulator(operator, getDefaultContentionLevel());
	}

	public static FinishAccumulator<Double> newDoubleFinishAccumulator(NumberReductionOperator operator,
			ContentionLevel contentionLevel, DoubleAccumulationDeterminismPolicy determinismPolicy) {
		return getImpl().newDoubleFinishAccumulator(operator, contentionLevel, determinismPolicy);
	}

	public static FinishAccumulator<Double> newDoubleFinishAccumulator(NumberReductionOperator operator,
			ContentionLevel contentionLevel) {
		return newDoubleFinishAccumulator(operator, contentionLevel, DoubleAccumulationDeterminismPolicy.DETERMINISTIC);
	}

	public static FinishAccumulator<Double> newDoubleFinishAccumulator(NumberReductionOperator operator) {
		return newDoubleFinishAccumulator(operator, getDefaultContentionLevel());
	}

	public static <T> FinishAccumulator<T> newReducerFinishAccumulator(AccumulatorReducer<T> reducer,
			ContentionLevel contentionLevel) {
		return getImpl().newReducerFinishAccumulator(reducer, contentionLevel);
	}

	public static <T> FinishAccumulator<T> newReducerFinishAccumulator(AccumulatorReducer<T> reducer) {
		return newReducerFinishAccumulator(reducer, getDefaultContentionLevel());
	}

	private static void launch(CheckedRunnable body) throws InterruptedException, ExecutionException {
		implAtom.get().launch(body);
	}

	private static void launchApp(V5Impl impl, CheckedRunnable body,
			BiConsumer<Metrics, Bookkeeping> preFinalizeCallback) {
		if (implAtom.compareAndSet(null, impl)) {
			try {
				launch(body);
				if (preFinalizeCallback != null) {
					preFinalizeCallback.accept(impl.getMetrics(), impl.getBookkeeping().get());
				}
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException(e);
			} finally {
				implAtom.set(null);
			}
		} else {
			throw new RuntimeException("previous launchApp has not completed");
		}
	}

	public static void shutdownIfNecessary() {
		V5Impl prevImpl = implAtom.get();
		if(prevImpl != null) {
			if (implAtom.compareAndSet(prevImpl, null)) {
				prevImpl.shutdownNow();
			}
		}
	}

	public static void launchApp(CheckedRunnable body) {
		launchApp(new ExecutorV5Impl(ForkJoinPool.commonPool()), body, null);
	}

	public static <T> T launchAppWithReturn(CheckedCallable<T> body) {
		class MutuableT {
			private T value;
		}
		MutuableT mutuableT = new MutuableT();
		launchApp(() -> {
			mutuableT.value = body.call();
		});
		return mutuableT.value;
	}

	public static void launchApp(SystemPropertiesOption systemPropertiesOption, CheckedRunnable body) {
		launchApp(systemPropertiesOption, body, null);
	}

	public static void launchApp(SystemPropertiesOption systemPropertiesOption, CheckedRunnable body,
			BiConsumer<Metrics, Bookkeeping> preFinalizeCallback) {
		boolean isLinearized = systemPropertiesOption != null ? systemPropertiesOption.isLinearized() : false;
		if (isLinearized) {
			launchApp(new SerialV5Impl(), body, preFinalizeCallback);
		} else {
			ExecutorService executorService;
			Integer numWorkerThreads = systemPropertiesOption != null ? systemPropertiesOption.getNumWorkerThreads()
					: null;
			if (systemPropertiesOption.isCachedThreadPoolDesired()) {
				executorService = Executors.newCachedThreadPool();
			} else if (systemPropertiesOption.isWorkStealingThreadPoolDesired()) {
				if (numWorkerThreads != null) {
					executorService = Executors.newWorkStealingPool(numWorkerThreads.intValue());
				} else {
					executorService = Executors.newWorkStealingPool();
				}
			} else {
				if (numWorkerThreads != null) {
					executorService = Executors.newFixedThreadPool(numWorkerThreads.intValue());
				} else {
					executorService = ForkJoinPool.commonPool();
				}
			}
			V5Impl impl = systemPropertiesOption.isBookkeepingDesired()
					? new BookkeepingExecutorXV5Impl(executorService)
					: new ExecutorV5Impl(executorService);
			launchApp(impl, body, preFinalizeCallback);
			executorService.shutdown();
		}
	}

	public static Optional<Activity> getCurrentActivity() {
		return getImpl().getCurrentActivity();
	}

	public static void resetBookkeeping() {
		getImpl().resetBookkeeping();
	}

}

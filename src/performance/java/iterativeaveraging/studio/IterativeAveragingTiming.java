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

package iterativeaveraging.studio;

import static edu.wustl.cse231s.v5.V5.launchApp;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import com.google.common.base.Supplier;

import edu.wustl.cse231s.NotYetImplementedException;
import edu.wustl.cse231s.timing.ImmutableTimer;
import edu.wustl.cse231s.v5.options.SystemPropertiesOption;
import iterativeaveraging.challenge.FuzzyPointToPointPhasedParallelIterativeAverager;
import iterativeaveraging.challenge.PointToPointPhasedParallelIterativeAverager;
import iterativeaveraging.core.IterativeAverager;
import iterativeaveraging.warmup.SequentialIterativeAverager;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class IterativeAveragingTiming {
	private static int SLICE_COUNT = Runtime.getRuntime().availableProcessors();

	private static enum IA implements Supplier<IterativeAverager> {
		SEQUENTIAL() {
			@Override
			public IterativeAverager get() {
				return new SequentialIterativeAverager();
			}
		},
		PARALLEL() {
			@Override
			public IterativeAverager get() {
				return new ParallelIterativeAverager(SLICE_COUNT);
			}
		},
		PHASED() {
			@Override
			public IterativeAverager get() {
				return new PhasedParallelIterativeAverager(SLICE_COUNT);
			}
		},
		FUZZY() {
			@Override
			public IterativeAverager get() {
				return new FuzzyPhasedParallelIterativeAverager(SLICE_COUNT);
			}
		},
		POINT_TO_POINT() {
			@Override
			public IterativeAverager get() {
				return new PointToPointPhasedParallelIterativeAverager(SLICE_COUNT);
			}
		},
		FUZZY_POINT_TO_POINT() {
			@Override
			public IterativeAverager get() {
				return new FuzzyPointToPointPhasedParallelIterativeAverager(SLICE_COUNT);
			}
		};
	}

	public static void main(String[] args) throws Exception {
		final int TASKS_PER_PROCESSOR = 1;
		final int TASK_COUNT = Runtime.getRuntime().availableProcessors() * TASKS_PER_PROCESSOR;
		final int INDICES_PER_TASK = 2_000;
		final int ARRAY_LENGTH = (TASK_COUNT * INDICES_PER_TASK) + 2;

		final boolean IS_COMMON_POOL_DESIRED = false;
		final boolean IS_FIXED = true;
		ExecutorService executorService = IS_COMMON_POOL_DESIRED ? ForkJoinPool.commonPool()
				: IS_FIXED ? Executors.newFixedThreadPool(TASK_COUNT) : Executors.newCachedThreadPool();

		final int ITERATION_COUNT = 50_000;
		double[] original = new double[ARRAY_LENGTH];
		original[original.length - 1] = 1.0;
		SystemPropertiesOption.Builder builder = new SystemPropertiesOption.Builder();
		if (IS_COMMON_POOL_DESIRED) {
			// pass
		} else {
			if (IS_FIXED) {
				builder.numWorkerThreads(TASK_COUNT);
			} else {
				builder.isCachedThreadPoolDesired(true);
			}
		}
		SystemPropertiesOption systemPropertiesOption = builder.build();
		launchApp(systemPropertiesOption, () -> {
			final int RUN_COUNT = 10;
			for (int runIndex = 0; runIndex < RUN_COUNT; runIndex++) {

				for (IA ia : IA.values()) {
					time(ia, original, TASK_COUNT, ITERATION_COUNT);
				}

				System.out.println();
			}
		});

		if (IS_COMMON_POOL_DESIRED) {
			// pass
		} else {
			executorService.shutdown();
		}
	}

	private static String FORMAT = "%52s";

	private static void time(Supplier<IterativeAverager> supplier, double[] original, int numSlices, int iterationCount)
			throws InterruptedException, ExecutionException {
		IterativeAverager iterativeAverager = supplier.get();
		String prefix = String.format(FORMAT, iterativeAverager.toString());
		ImmutableTimer timer = new ImmutableTimer(prefix);
		try {
			iterativeAverager.iterativelyAverage(original, iterationCount);
			timer.markAndPrintResults();
		} catch (NotYetImplementedException nyie) {
			timer.mark();
			System.out.println(prefix + ";        NotYetImplemented");
		}
	}
}

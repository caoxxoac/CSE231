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

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.function.Consumer;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runners.MethodSorters;

import edu.wustl.cse231s.junit.JUnitUtils;
import edu.wustl.cse231s.v5.api.Bookkeeping;
import edu.wustl.cse231s.v5.bookkeep.BookkeepingUtils;
import edu.wustl.cse231s.v5.options.SystemPropertiesOption;
import iterativeaveraging.core.IterativeAverager;
import iterativeaveraging.warmup.SequentialIterativeAverager;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link ParallelIterativeAverager#iterativelyAverage(List, double[], double[], int)}
 *         {@link PhasedParallelIterativeAverager#iterativelyAverage(List, double[], double[], int)}
 *         {@link FuzzyPhasedParallelIterativeAverager#iterativelyAverage(List, double[], double[], int)}
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IterativeAveragerParallelismTest {
	private static final int NUM_ITERATIONS = 100;
	private static final int NUM_SLICES = Runtime.getRuntime().availableProcessors();

	private void bookkeep(IterativeAverager iterativeAverager, Consumer<Bookkeeping> bookkeepingConsumer) {
		int indicesPerSlice = 2;
		int length = (NUM_SLICES * indicesPerSlice) + 2;

		double[] original = new double[length];
		original[original.length - 1] = 1.0;

		BookkeepingUtils.bookkeep(new SystemPropertiesOption.Builder().numWorkerThreads(NUM_SLICES), () -> {
			iterativeAverager.iterativelyAverage(original, NUM_ITERATIONS);
		}, bookkeepingConsumer);
	}

	@Rule
	public TestRule timeout = JUnitUtils.createTimeoutRule();

//	@Test
//	public void testA_sequential() {
//		bookkeep(new SequentialIterativeAverager(), (bookkeeping) -> {
//			assertEquals(0, bookkeeping.getFutureInvocationCount());
//			assertEquals(0, bookkeeping.getTaskCount());
//		});
//	}

	@Test
	public void testB_parallel() {
		bookkeep(new ParallelIterativeAverager(NUM_SLICES), (bookkeeping) -> {
			assertEquals(NUM_ITERATIONS, bookkeeping.getForasyncTotalInvocationCount());
			assertEquals(NUM_ITERATIONS, bookkeeping.getNonAccumulatorFinishInvocationCount());
			assertEquals(NUM_ITERATIONS * NUM_SLICES, bookkeeping.getAsyncViaForasyncCount());
		});
	}

	private void testPhased(IterativeAverager iterativeAverager) {
		bookkeep(iterativeAverager, (bookkeeping) -> {
			assertEquals(1, bookkeeping.getForasyncTotalInvocationCount());
			assertEquals(1, bookkeeping.getNonAccumulatorFinishInvocationCount());
			assertEquals(NUM_SLICES, bookkeeping.getAsyncViaForasyncCount());
		});
	}

	@Test
	public void testC_phasedParallel() {
		testPhased(new PhasedParallelIterativeAverager(NUM_SLICES));
	}

	@Test
	public void testD_fuzzyPhasedParallel() {
		testPhased(new FuzzyPhasedParallelIterativeAverager(NUM_SLICES));
	}
}

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

import java.util.List;

import org.junit.Rule;
import org.junit.rules.TestRule;

import edu.wustl.cse231s.junit.JUnitUtils;
import edu.wustl.cse231s.print.AbstractNoPrintingTest;
import edu.wustl.cse231s.v5.options.SystemPropertiesOption;
import iterativeaveraging.core.IterativeAverager;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link ParallelIterativeAverager#iterativelyAverage(List, double[], double[], int)}
 *         {@link PhasedParallelIterativeAverager#iterativelyAverage(List, double[], double[], int)}
 */
public class NoPrintingTest extends AbstractNoPrintingTest {
	private void testIterativeAverager(IterativeAverager iterativeAverager) {
		int numSlices = Runtime.getRuntime().availableProcessors();
		int indicesPerSlice = 2;
		int length = (numSlices * indicesPerSlice) + 2;
		int iterationCount = 100;

		double[] original = new double[length];
		original[original.length - 1] = 1.0;
		launchApp(new SystemPropertiesOption.Builder().numWorkerThreads(numSlices).build(), () -> {
			iterativeAverager.iterativelyAverage(original, iterationCount);
		});
	}

	@Rule
	public TestRule timeout = JUnitUtils.createTimeoutRule();

	@Override
	protected void testKernel() {
		final int SLICE_COUNT = Runtime.getRuntime().availableProcessors();
		testIterativeAverager(new ParallelIterativeAverager(SLICE_COUNT));
		testIterativeAverager(new PhasedParallelIterativeAverager(SLICE_COUNT));
		testIterativeAverager(new FuzzyPhasedParallelIterativeAverager(SLICE_COUNT));
	}
}

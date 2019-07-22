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
import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.wustl.cse231s.junit.JUnitUtils;
import edu.wustl.cse231s.phasable.PhasableDoubleArrays;
import edu.wustl.cse231s.v5.options.SystemPropertiesOption;
import iterativeaveraging.core.IterativeAverager;
import iterativeaveraging.warmup.SequentialIterativeAverager;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link ParallelIterativeAverager#iterativelyAverage(double[], int)}
 *         {@link PhasedParallelIterativeAverager#iterativelyAverage(double[], int)}
 *         {@link FuzzyPhasedParallelIterativeAverager#iterativelyAverage(double[], int)}
 */
@RunWith(Parameterized.class)
public class IterativeAveragerTest {
	private final IterativeAverager iterativeAverager;
	private final double[] original;
	private final double[] expected;
	private final int numSlices;
	private final int iterationCount;

	public IterativeAveragerTest(IterativeAverager iterativeAverager, ArrayFiller arrayFiller, int length,
			int numSlices, int iterationCount) {
		this.iterativeAverager = iterativeAverager;
		this.original = new double[length];
		arrayFiller.fill(this.original);
		// this.Expected = Arrays.copyOf(original, original.length);
		PhasableDoubleArrays data = new PhasableDoubleArrays(original);
		for (int iteration = 0; iteration < iterationCount; iteration++) {
			double[] arrayPrev = data.getSrcForPhase(iteration);
			double[] arrayNext = data.getDstForPhase(iteration);
			arrayNext[0] = arrayPrev[0];
			arrayNext[arrayNext.length - 1] = arrayPrev[arrayPrev.length - 1];
			for (int index = 1; index < arrayPrev.length - 1; index++) {
				arrayNext[index] = (arrayPrev[index - 1] + arrayPrev[index + 1]) * 0.5;
			}
		}
		this.expected = data.getSrcForPhase(iterationCount);
		this.numSlices = numSlices;
		this.iterationCount = iterationCount;
	}

	@Rule
	public TestRule timeout = JUnitUtils.createTimeoutRule();

	@Test
	public void test() {
		double[] testArray = Arrays.copyOf(original, original.length);
		launchApp(new SystemPropertiesOption.Builder().numWorkerThreads(numSlices).build(), () -> {
			double[] testResult = this.iterativeAverager.iterativelyAverage(testArray, iterationCount);
			assertArrayEquals(expected, testResult, 0.0);

		});
	}

	private static enum ArrayFiller {
		ONE_IN_LAST_INDEX() {
			@Override
			public void fill(double[] array) {
				array[array.length - 1] = 1;
			}
		},
		RANDOM() {
			@Override
			public void fill(double[] array) {
				Random random = new Random();
				for (int i = 0; i < array.length; i++) {
					array[i] = random.nextDouble();
				}
			}
		};
		public abstract void fill(double[] array);
	};

	@Parameters(name = "{0}; {1}; length={2}; numSlices={3}; iterations={4}")
	public static Collection<Object[]> getConstructorArguments() {
		int numSlices = Runtime.getRuntime().availableProcessors();
		List<Object[]> list = new LinkedList<>();
		for (IterativeAverager iterativeAverager : new IterativeAverager[] { 
				//new SequentialIterativeAverager(),
				new ParallelIterativeAverager(numSlices), new PhasedParallelIterativeAverager(numSlices),
				new FuzzyPhasedParallelIterativeAverager(numSlices) }) {
			for (ArrayFiller arrayFiller : ArrayFiller.values()) {
				for (int indicesPerSlice : new int[] { 1, 2, 10, 71 }) {
					int length = (numSlices * indicesPerSlice) + 2;
					for (int iterationCount : new int[] { 1, 2, 42, 1_000 }) {
						list.add(new Object[] { iterativeAverager, arrayFiller, length, numSlices, iterationCount });
					}
				}
			}
		}
		return list;
	}
}

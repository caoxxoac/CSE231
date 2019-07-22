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
package scan.studio;

import static edu.wustl.cse231s.v5.V5.launchApp;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

import java.util.concurrent.ExecutionException;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import org.junit.Test;

import edu.wustl.cse231s.array.SwappableIntArrays;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link StepEfficientParallelScan#sumScanInclusive(int[])}
 */
public class StepEfficientParallelScanIntermediateResultsTest {
	@Test
	public void test() throws InterruptedException, ExecutionException {
		int[] data = { 1, 1, 1, 1, 1, 1, 1, 1 };

		int[][] expectedSrcs = { data, { 1, 2, 2, 2, 2, 2, 2, 2 }, { 1, 2, 3, 4, 4, 4, 4, 4 }, };

		int[][] expectedDsts = { expectedSrcs[1], expectedSrcs[2], { 1, 2, 3, 4, 5, 6, 7, 8 } };

		class TestSwappableIntArrays extends SwappableIntArrays {
			private int testPhase = 0;

			public TestSwappableIntArrays(int registrations, int[] data) {
				super(registrations, data);
			}

			@Override
			public void swap() {
				assertThat(testPhase, lessThanOrEqualTo(expectedSrcs.length));
				String message = String.format("index: %d", testPhase);
				assertArrayEquals(message, expectedSrcs[testPhase], this.getSrc());
				assertArrayEquals(message, expectedDsts[testPhase], this.getDst());
				testPhase++;
				super.swap();
			}
		}

		TestSwappableIntArrays arraysHolder = new TestSwappableIntArrays(1, data);
		launchApp(() -> {
			StepEfficientParallelScan.sumScanInclusive(arraysHolder);
		});
	}
}

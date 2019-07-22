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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

import org.junit.Test;

import edu.wustl.cse231s.v5.api.Bookkeeping;
import edu.wustl.cse231s.v5.bookkeep.BookkeepingUtils;
import scan.util.TestScan;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public abstract class AbstractScanTest {
	private final IntUnaryOperator valueGenerator;
	private final int length;
	private final TestScan scan;

	public AbstractScanTest(IntUnaryOperator valueGenerator, int length, TestScan scan) {
		this.valueGenerator = valueGenerator;
		this.length = length;
		this.scan = scan;
	}

	protected abstract void checkParallelism(Bookkeeping bookkeep, int length);

	@Test
	public void test() {
		int[] original = new int[this.length];
		for (int i = 0; i < original.length; i++) {
			original[i] = this.valueGenerator.applyAsInt(i);
		}

		int[] data = Arrays.copyOf(original, original.length);
		int[] buffer = Arrays.copyOf(original, original.length);

		Arrays.parallelPrefix(buffer, Integer::sum);
		int[] expected;
		if (scan.isInclusive()) {
			expected = buffer;
		} else {
			expected = IntStream.range(0, data.length).map((i) -> buffer[i] - data[i]).toArray();
		}

		BookkeepingUtils.bookkeep(() -> {
			int[] actuals = this.scan.sumScan(data);
			if (scan.isInPlace()) {
				assertSame(data, actuals);
			} else {
				assertArrayEquals(
						"Input data has been mutated.  Do NOT mutate the input.  Return a new array with the result.\n",
						original, data);
			}
			assertArrayEquals("Returned result is incorrect.\ninput data: " + Arrays.toString(original) + "\nexpected result: " + Arrays.toString(expected) + "\nactual result: " + Arrays.toString(actuals) + "\n", expected, actuals);
		}, (bookkeeping) -> {
			checkParallelism(bookkeeping, this.length);
		});
	}
}

/*******************************************************************************
 * Copyright (C) 2016-2017 Dennis Cosgrove
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
package slice.studio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.wustl.cse231s.junit.JUnitUtils;
import slice.core.Slice;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link Slices}
 */
@RunWith(Parameterized.class)
public class StrictSlicesComprehensiveTest {
	private final byte[] array;
	private final int sliceCount;

	public StrictSlicesComprehensiveTest(int arrayLength, int sliceCount) {
		this.array = new byte[arrayLength];
		this.sliceCount = sliceCount;
	}

	@Rule
	public TestRule timeout = JUnitUtils.createTimeoutRule();

	@Test
	public void test() {
		List<Slice<byte[]>> slices = Slices.createNSlices(array, sliceCount);
		assertNotNull("Your solution returned a null object", slices);
		assertEquals("You did return the correct number of slices", sliceCount, slices.size());

		Iterator<Slice<byte[]>> iterator = slices.iterator();

		Slice<byte[]> slice0 = iterator.next();
		assertNotNull("One of the slices is null", slice0);
		assertSame(
				"The slice.getOriginalUnslicedData() did not return the data passed in. "
						+ "Make sure you are passing through the full data array provided, and NOT a copy of it",
				array, slice0.getOriginalUnslicedData());
		assertEquals("The IDs of the slices should start at zero and increase numerically", 0,
				slice0.getSliceIndexId());
		assertEquals("A slice.minInclusive was incorrect", 0, slice0.getMinInclusive());

		int prevMax = slice0.getMaxExclusive();
		int prevSliceLength = prevMax;
		boolean hasDecreasedByOneAlready = false;

		int indexId = 1;
		while (iterator.hasNext()) {
			Slice<byte[]> slice = iterator.next();
			assertNotNull("One of the slices is null", slice);
			assertSame(
					"The slice.getOriginalUnslicedData() did not return the data passed in. "
							+ "Make sure you are passing through the full data array provided, and NOT a copy of it",
					array, slice.getOriginalUnslicedData());
			assertEquals("The IDs of the slices should start at zero and increase numerically", indexId,
					slice.getSliceIndexId());
			assertEquals("A slice.minInclusive was incorrect", prevMax, slice.getMinInclusive());
			int sliceLength = slice.getMaxExclusive() - slice.getMinInclusive();
			if (sliceLength == prevSliceLength - 1) {
				assertFalse("The sizes of slices should be consistent", hasDecreasedByOneAlready);
				hasDecreasedByOneAlready = true;
				prevSliceLength = sliceLength;
			} else {
				assertEquals("The sizes of slices should be consistent", prevSliceLength, sliceLength);
			}
			prevMax = slice.getMaxExclusive();
			indexId++;
		}
		assertEquals("The final slice's maxExclusive should be the length of the passed in array", array.length,
				prevMax);
	}

	@Parameters(name = "length={0}; sliceCount={1}")
	public static Collection<Object[]> getConstructorArguments() {
		List<Object[]> result = new LinkedList<>();
		for (int length = 71; length < 231; length += 11) {
			for (int sliceCount : new int[] { 1, 2, 3, 8, 17, 101 }) {
				result.add(new Object[] { length, sliceCount });
			}
		}
		return result;
	}

}

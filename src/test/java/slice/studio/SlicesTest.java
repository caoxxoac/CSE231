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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Collection;
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
 * @author Finn Voichick
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link Slices}
 */
@RunWith(Parameterized.class)
public class SlicesTest {
	private final Object[] data;
	private final int numSlices;

	public SlicesTest(int length, int numSlices) {
		this.data = new Object[length];
		this.numSlices = numSlices;
	}

	@Rule
	public TestRule timeout = JUnitUtils.createTimeoutRule();

	@Test
	public void test() {
		List<Slice<Object[]>> slices = Slices.createNSlices(data, numSlices);
		assertNotNull("Your solution returned a null object", slices);
		assertEquals("You did return the correct number of slices", numSlices, slices.size());
		for (Slice<Object[]> slice : slices) {
			assertNotNull("One of the slices is null", slice);
		}
		for (Slice<Object[]> slice : slices) {
			assertSame(
					"The slice.getOriginalUnslicedData() did not return the data passed in. "
							+ "Make sure you are passing through the full data array provided, and NOT a copy of it",
					data, slice.getOriginalUnslicedData());
		}
		int expectedId = 0;
		for (Slice<Object[]> slice : slices) {
			assertEquals("The IDs of the slices should start at zero and increase numerically", expectedId,
					slice.getSliceIndexId());
			expectedId++;
		}
		if (numSlices > 0) {
			int expectedMin = 0;
			for (Slice<Object[]> slice : slices) {
				assertEquals("A slice.minInclusive was incorrect", expectedMin, slice.getMinInclusive());
				expectedMin = slice.getMaxExclusive();
			}
			int expectedMax = expectedMin;
			assertEquals("The final slice's maxExclusive should be the length of the passed in array", expectedMax,
					data.length);
		}
	}

	@Parameters(name = "length={0}; numSlices={1}")
	public static Collection<Object[]> getConstructorArguments() {
		List<Object[]> result = new LinkedList<>();
		result.add(new Object[] { 10, 2 });
		result.add(new Object[] { 11, 2 });
		result.add(new Object[] { 100, 10 });
		result.add(new Object[] { 105, 10 });
		return result;
	}
}

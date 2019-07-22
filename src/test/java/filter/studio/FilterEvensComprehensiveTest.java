/*******************************************************************************
 * Copyright (C) 2016-2019 Dennis Cosgrove
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
package filter.studio;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
@RunWith(Parameterized.class)
public class FilterEvensComprehensiveTest {
	private final List<Integer> original;
	private final List<Integer> expected;

	public FilterEvensComprehensiveTest(List<Integer> original, List<Integer> expected) {
		this.original = original;
		this.expected = expected;
	}

	@Test
	public void test() {
		List<Integer> copy = new ArrayList<>(original);
		List<Integer> actual = FilterApps.filterEvens(copy);
		assertEquals("should not mutate input", original, copy);
		assertEquals(expected, actual);
	}

	private static Object[] createRandom(int size) {
		Random random = new Random();
		List<Integer> original = new ArrayList<>(size);
		List<Integer> expected = new LinkedList<>();
		for (int i = 0; i < size; ++i) {
			int r = random.nextInt(1_000);
			original.add(r);
			if((r & 1)==0) {
				expected.add(r);
			}
		}
		return new Object[] {original, expected};
	}

	@Parameters(name = "original: {0}; expected: {1}")
	public static Collection<Object[]> getConstructorArguments() {
		List<Object[]> result = new LinkedList<>();
		result.add(new Object[] { Arrays.asList(), Arrays.asList() });
		result.add(new Object[] { Arrays.asList(1), Arrays.asList() });
		result.add(new Object[] { Arrays.asList(2), Arrays.asList(2) });
		result.add(new Object[] { Arrays.asList(1, 2), Arrays.asList(2) });
		result.add(new Object[] { Arrays.asList(2, 3), Arrays.asList(2) });
		result.add(new Object[] { Arrays.asList(1, 2, 3), Arrays.asList(2) });
		result.add(new Object[] { Arrays.asList(1, 2, 3, 4), Arrays.asList(2, 4) });
		result.add(new Object[] { Arrays.asList(1, 2, 3, 4, 5), Arrays.asList(2, 4) });
		
		for(int i=0; i<100; ++i) {
			result.add(createRandom(5));
		}
		return result;
	}
}

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
import java.util.function.Predicate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
@RunWith(Parameterized.class)
public class FilterComprehensiveTest {
	private final Predicate<String> predicate;
	private final List<String> original;
	private final List<String> expected;

	public FilterComprehensiveTest(Predicate<String> predicate, List<String> original, List<String> expected) {
		this.predicate = predicate;
		this.original = original;
		this.expected = expected;
	}

	@Test
	public void test() {
		List<String> copy = new ArrayList<>(original);

		List<String> actual = FilterUtils.filter(predicate, copy);

		assertEquals("should not mutate input", original, copy);
		assertEquals(expected, actual);
	}

	private static enum TestPredicates implements Predicate<String> {
		CONTAINS_X_IGNORING_CASE {
			@Override
			public boolean test(String s) {
				String lowerCase = s.toLowerCase();
				return lowerCase.indexOf('x') != -1;
			}
		},
		LENGTH_LONGER_THAN_3 {
			@Override
			public boolean test(String s) {
				return s.length() > 3;
			}
		};
	}

	@Parameters(name = "{0}; original: {1}; expected: {2}")
	public static Collection<Object[]> getConstructorArguments() {
		List<Object[]> result = new LinkedList<>();
		result.add(new Object[] { TestPredicates.CONTAINS_X_IGNORING_CASE,
				Arrays.asList(),
				Arrays.asList() });
		result.add(new Object[] { TestPredicates.CONTAINS_X_IGNORING_CASE,
				Arrays.asList("Don Quixote"),
				Arrays.asList("Don Quixote") });
		result.add(new Object[] { TestPredicates.CONTAINS_X_IGNORING_CASE,
				Arrays.asList("Sancho Panza"),
				Arrays.asList() });
		result.add(new Object[] { TestPredicates.CONTAINS_X_IGNORING_CASE,
				Arrays.asList("Don Quixote", "Sancho Panza"),
				Arrays.asList("Don Quixote") });
		result.add(new Object[] { TestPredicates.CONTAINS_X_IGNORING_CASE,
				Arrays.asList("Sancho Panza", "Don Quixote"),
				Arrays.asList("Don Quixote") });
		result.add(new Object[] { TestPredicates.CONTAINS_X_IGNORING_CASE,
				Arrays.asList("Sancho Panza", "Dapple", "Don Quixote", "Rocinante"),
				Arrays.asList("Don Quixote") });
		result.add(new Object[] { TestPredicates.LENGTH_LONGER_THAN_3,
				Arrays.asList(),
				Arrays.asList() });
		result.add(new Object[] { TestPredicates.LENGTH_LONGER_THAN_3,
				Arrays.asList("abcd"),
				Arrays.asList("abcd") });
		result.add(new Object[] { TestPredicates.LENGTH_LONGER_THAN_3,
				Arrays.asList("a"),
				Arrays.asList() });
		result.add(new Object[] { TestPredicates.LENGTH_LONGER_THAN_3,
				Arrays.asList("a", "bcde"),
				Arrays.asList("bcde") });
		result.add(new Object[] { TestPredicates.LENGTH_LONGER_THAN_3,
				Arrays.asList("a", "bcde", "f"),
				Arrays.asList("bcde") });
		result.add(new Object[] { TestPredicates.LENGTH_LONGER_THAN_3,
				Arrays.asList("abcde", "f", "ghijk"),
				Arrays.asList("abcde", "ghijk") });
		result.add(new Object[] { TestPredicates.LENGTH_LONGER_THAN_3,
				Arrays.asList("abcde", "f", "ghijk", "l"),
				Arrays.asList("abcde", "ghijk") });
		result.add(new Object[] { TestPredicates.LENGTH_LONGER_THAN_3,
				Arrays.asList("a", "bcde", "f", "ghijk", "l"),
				Arrays.asList("bcde", "ghijk") });
		result.add(new Object[] { TestPredicates.LENGTH_LONGER_THAN_3,
				Arrays.asList("abcd", "efg", "hijk", "lmnop", "qrs", "tuv", "wx", "y and z"),
				Arrays.asList("abcd", "hijk", "lmnop", "y and z") });
		return result;
	}
}

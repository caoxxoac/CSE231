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
public class FilterWordsWhichContainAllVowelsComprehensiveTest {
	private final List<String> original;
	private final List<String> expected;

	public FilterWordsWhichContainAllVowelsComprehensiveTest(List<String> original, List<String> expected) {
		this.original = original;
		this.expected = expected;
	}

	@Test
	public void test() {
		List<String> copy = new ArrayList<>(original);
		List<String> actual = FilterApps.filterWordsWhichContainAllVowels(copy);
		assertEquals("should not mutate input", original, copy);
		assertEquals(expected, actual);
	}

	private static char nextRandomLetter() {
		Random random = new Random();
		return (char) ('a' + random.nextInt(26)); 
	}
	
	private static String nextRandomString() {
		return new String(new char[] { nextRandomLetter(), nextRandomLetter(), nextRandomLetter() });
	}

	private static Object[] createRandom(int size) {
		Random random = new Random();
		List<String> original = new ArrayList<>(size);
		List<String> expected = new LinkedList<>();

		List<String> allWordsContainAllVowels = AllWordsContainAllVowelsTest.getAllWordsContainAllVowelsList();
		for (int i = 0; i < size; ++i) {
			String s;
			if (random.nextBoolean()) {
				s = nextRandomString();
			} else {
				s = allWordsContainAllVowels.get(random.nextInt(allWordsContainAllVowels.size()));
				expected.add(s);
			}
			original.add(s);
		}
		return new Object[] { original, expected };
	}

	@Parameters(name = "original: {0}; expected: {1}")
	public static Collection<Object[]> getConstructorArguments() {
		List<Object[]> result = new LinkedList<>();
		result.add(new Object[] { Arrays.asList(), Arrays.asList() });
		result.add(new Object[] { Arrays.asList("b"), Arrays.asList() });
		result.add(new Object[] { Arrays.asList("a"), Arrays.asList() });
		result.add(new Object[] { Arrays.asList("e"), Arrays.asList() });
		result.add(new Object[] { Arrays.asList("i"), Arrays.asList() });
		result.add(new Object[] { Arrays.asList("o"), Arrays.asList() });
		result.add(new Object[] { Arrays.asList("u"), Arrays.asList() });
		result.add(new Object[] { Arrays.asList("aeiou"), Arrays.asList("aeiou") });
		result.add(new Object[] { Arrays.asList("b", "aeiou"), Arrays.asList("aeiou") });
		result.add(new Object[] { Arrays.asList("aeiou", "b"), Arrays.asList("aeiou") });
		for (int i = 0; i < 100; ++i) {
			result.add(createRandom(5));
		}
		return result;
	}
}

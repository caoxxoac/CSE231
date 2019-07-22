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
package threshold.lab;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import count.lab.rubric.CountRubric;
import edu.wustl.cse231s.junit.JUnitUtils;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link GreaterThanThresholdPredicate#test(int)}
 */
@CountRubric(CountRubric.Category.THRESHOLD)
@RunWith(Parameterized.class)
public class ThresholdPredicateTest {
	private final int threshold;
	private final int rangeLength;

	public ThresholdPredicateTest(int threshold, int rangeLength) {
		this.threshold = threshold;
		this.rangeLength = rangeLength;
	}

	@Test
	public void test() {
		GreaterThanThresholdPredicate predicate = new GreaterThanThresholdPredicate(threshold);
		assertEquals(threshold, predicate.getThreshold());

		boolean expected = this.rangeLength > this.threshold;
		boolean actual = predicate.test(rangeLength);
		assertEquals(String.format("((rangeLength: %d)>(threshold: %d))", rangeLength, threshold), expected, actual);
	}

	@Rule
	public TestRule timeout = JUnitUtils.createTimeoutRule();

	@Parameters(name = "threshold={0}; rangeLength={1}")
	public static Collection<Object[]> getConstructorArguments() {
		List<Integer> thresholds = Arrays.asList(10, 71, 231);
		List<Object[]> result = new LinkedList<>();
		for (int threshold : thresholds) {
			result.add(new Object[] { threshold, 0 });
			result.add(new Object[] { threshold, 1 });
			result.add(new Object[] { threshold, 7 });
			result.add(new Object[] { threshold, threshold - 1 });
			result.add(new Object[] { threshold, threshold });
			result.add(new Object[] { threshold, threshold + 1 });
			result.add(new Object[] { threshold, 1_000 });
		}
		return result;
	}
}

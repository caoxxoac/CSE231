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
 *         {@link GreaterThanThresholdPredicate#GreaterThanThresholdPredicate(int)}
 */
@CountRubric(CountRubric.Category.THRESHOLD)
@RunWith(Parameterized.class)
public class ThresholdPredicateConstructorTest {
	private final int threshold;

	public ThresholdPredicateConstructorTest(int threshold) {
		this.threshold = threshold;
	}

	@Test
	public void testConstructor() {
		GreaterThanThresholdPredicate predicate = new GreaterThanThresholdPredicate(this.threshold);
		assertEquals(this.threshold, predicate.getThreshold());
	}

	@Rule
	public TestRule timeout = JUnitUtils.createTimeoutRule();

	@Parameters(name = "threshold={0}")
	public static Collection<Object[]> getConstructorArguments() {
		List<Integer> thresholds = Arrays.asList(1, 2, 10, 71, 231);
		return JUnitUtils.toParameterizedArguments(thresholds);
	}
}

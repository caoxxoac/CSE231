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
package mapreduce.apps.windbag.studio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.wustl.cse231s.junit.JUnitUtils;
import mapreduce.apps.play.core.PlayLine;
import mapreduce.apps.play.core.PlayRole;
import mapreduce.apps.play.io.PlayResource;
import mapreduce.core.AbstractMRTest;
import mapreduce.core.CollectorSolution;
import mapreduce.core.FrameworkSolution;
import mapreduce.core.MapperSolution;
import mapreduce.core.TestApplication;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
@RunWith(Parameterized.class)
public class WindbagTest extends AbstractMRTest<PlayLine, PlayRole, Boolean, Number> {
	public WindbagTest(TestApplication testApplication, MapperSolution mapperSolution,
			CollectorSolution collectorSolution) {
		super(testApplication, FrameworkSolution.INSTRUCTOR, mapperSolution, collectorSolution);
	}

	@Rule
	public TestRule timeout = JUnitUtils.createTimeoutRule();

	@Test
	public void test() {
		this.testInput(PlayResource.HAMLET.getLines());
	}

	@Parameters(name = "{0}; mapper: {1} collector: {2}")
	public static Collection<Object[]> getConstructorArguments() {
		List<Object[]> result = new ArrayList<>();
		for (TestApplication testApplication : Arrays.asList(TestApplication.WINDBAG_COUNT,
				TestApplication.WINDBAG_PORTION)) {
			result.add(new Object[] { testApplication, MapperSolution.STUDENT, CollectorSolution.INSTRUCTOR });
			result.add(new Object[] { testApplication, MapperSolution.INSTRUCTOR, CollectorSolution.STUDENT_COMPLETE });
			result.add(new Object[] { testApplication, MapperSolution.STUDENT, CollectorSolution.STUDENT_COMPLETE });
		}
		return result;
	}

}

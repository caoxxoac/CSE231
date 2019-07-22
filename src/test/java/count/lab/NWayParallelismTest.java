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
package count.lab;

import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import count.lab.rubric.CountRubric;
import edu.wustl.cse231s.bioinformatics.Nucleobase;
import edu.wustl.cse231s.junit.JUnitUtils;
import edu.wustl.cse231s.v5.api.Bookkeeping;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link NucleobaseCounting#countParallelNWaySplit(byte[], Nucleobase, int)}
 */
@CountRubric(CountRubric.Category.NWAY_PARALLEL)
@RunWith(Parameterized.class)
public class NWayParallelismTest extends AbstractCountParallelismTest {
	private final int numTasks;

	public NWayParallelismTest(int numTasks) {
		super(100);
		this.numTasks = numTasks;
	}

	@Override
	protected void count(byte[] chromosome, Nucleobase targetNucleobase)
			throws InterruptedException, ExecutionException {
		NucleobaseCounting.countParallelNWaySplit(chromosome, targetNucleobase, numTasks);
	}

	@Override
	protected void checkBookkeep(Bookkeeping book) {
		assertEquals(1, book.getNonAccumulatorFinishInvocationCount(), 1);
		assertNotEquals("async only called <1> time; expected: <" + numTasks + "> or <" + (numTasks - 1) + ">", 1,
				book.getTaskCount());
		assertThat(book.getTaskCount(), either(is(numTasks)).or(is(numTasks - 1)));
	}

	@Parameters(name = "{0}")
	public static Collection<Object[]> getConstructorArguments() {
		return JUnitUtils.toParameterizedArguments(Arrays.asList(4, 10, 71));
	}
}

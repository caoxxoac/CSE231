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
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import count.lab.rubric.CountRubric;
import edu.wustl.cse231s.bioinformatics.Nucleobase;
import edu.wustl.cse231s.v5.api.Bookkeeping;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link NucleobaseCounting#countParallelDivideAndConquer(byte[], Nucleobase, java.util.function.IntPredicate)}
 */
@CountRubric(CountRubric.Category.DIVIDE_AND_CONQUER_PARALLEL)
@RunWith(Parameterized.class)
public class DivideAndConquerParallelismTest extends AbstractCountParallelismTest {
	private final int threshold;
	private final int expectedTasks;

	public DivideAndConquerParallelismTest(int length, int threshold, int expectedTasks) {
		super(length);
		this.threshold = threshold;
		this.expectedTasks = expectedTasks;
	}

	@Override
	protected void count(byte[] chromosome, Nucleobase targetNucleobase)
			throws InterruptedException, ExecutionException {
		NucleobaseCounting.countParallelDivideAndConquer(chromosome, targetNucleobase, (int length) -> {
			return length > threshold;
		});
	}

	@Override
	protected void checkBookkeep(Bookkeeping book) {
		assertEquals(expectedTasks, book.getNonAccumulatorFinishInvocationCount());
		assertThat(book.getTaskCount(), either(is(expectedTasks)).or(is(expectedTasks * 2)));
	}

	@Parameters(name = "length: {0}; threshold: {1}; expectedTasks: {2}")
	public static Collection<Object[]> getConstructorArguments() {
		List<Object[]> result = new LinkedList<>();
		result.add(new Object[] { 100, 20, 7 });
		return result;
	}
}

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

import static org.junit.Assert.assertNotEquals;

import java.util.concurrent.ExecutionException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import count.util.SequenceUtils;
import edu.wustl.cse231s.bioinformatics.Nucleobase;
import edu.wustl.cse231s.junit.JUnitUtils;
import edu.wustl.cse231s.v5.api.Bookkeeping;
import edu.wustl.cse231s.v5.bookkeep.BookkeepingUtils;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link NucleobaseCounting}
 */
public abstract class AbstractCountParallelismTest {
	private final int length;

	public AbstractCountParallelismTest(int length) {
		this.length = length;
	}

	protected abstract void count(byte[] chromosome, Nucleobase targetNucleobase)
			throws InterruptedException, ExecutionException;

	protected abstract void checkBookkeep(Bookkeeping book);

	@Test
	public void test() {
		Nucleobase nucleobase = Nucleobase.ADENINE;
		byte[] chromosome = SequenceUtils.createSequence(this.length, Nucleobase.ADENINE);
		BookkeepingUtils.bookkeep(() -> {
			count(chromosome, nucleobase);
		}, (book) -> {
			assertNotEquals("finish never invoked", 0, book.getNonAccumulatorFinishInvocationCount());
			assertNotEquals("async never invoked", 0, book.getTaskCount());
			checkBookkeep(book);
		});
	}

	@Rule
	public TestRule timeout = JUnitUtils.createTimeoutRule();
}

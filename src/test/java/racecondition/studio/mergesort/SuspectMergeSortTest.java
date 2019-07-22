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
package racecondition.studio.mergesort;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.wustl.cse231s.junit.JUnitUtils;
import edu.wustl.cse231s.v5.bookkeep.BookkeepingUtils;
import sort.core.SortUtils;
import sort.core.merge.SequentialCombiner;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link SuspectMergeSort#parallelMergeSort(int[], int, sort.core.merge.Combiner)}
 */
@RunWith(Parameterized.class)
public class SuspectMergeSortTest {
	private final int[] original;
	private final int[] expected;

	public SuspectMergeSortTest() {
		this.original = new int[100_000];
		Random random = new Random();
		for (int i = 0; i < original.length; i++) {
			this.original[i] = random.nextInt(1_000_000);
		}
		this.expected = Arrays.copyOf(this.original, this.original.length);
		Arrays.sort(this.expected);
	}

	@Rule
	public TestRule timeout = JUnitUtils.createTimeoutRule();

	@Test
	public void testSequential() throws InterruptedException, ExecutionException {
		int[] actual = Arrays.copyOf(this.original, this.original.length);
		SuspectMergeSort.sequentialMergeSort(actual, new SequentialCombiner(actual.length));
		assertTrue(SortUtils.isInSortedOrder(actual));
		assertArrayEquals(this.expected, actual);
	}

	@Test
	public void testParallel() {
		// int numProcessors = Runtime.getRuntime().availableProcessors();
		int numTasks = 80; // TODO
		int threshold = this.original.length / numTasks;
		BookkeepingUtils.bookkeep(() -> {
			int[] actual = Arrays.copyOf(this.original, this.original.length);
			SuspectMergeSort.parallelMergeSort(actual, threshold, new SequentialCombiner(actual.length));
			assertTrue(SortUtils.isInSortedOrder(actual));
			assertArrayEquals(this.expected, actual);
		}, (bookkeeping) -> {
			assertNotEquals("parallelism must not be removed", 0, bookkeeping.getTaskCount());
			assertEquals("unexpected task count", 127, bookkeeping.getTaskCount());
		});
	}

	@Parameters
	public static Collection<Object[]> getConstructorArguments() {
		return Arrays.asList(new Object[20][0]);
	}
}

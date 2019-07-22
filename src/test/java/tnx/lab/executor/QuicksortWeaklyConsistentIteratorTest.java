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
package tnx.lab.executor;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.wustl.cse231s.executors.BookkeepingExecutorService;
import edu.wustl.cse231s.sleep.SleepUtils;
import sort.core.RandomDataUtils;
import sort.core.quick.PivotInitialIndexSelector;
import sort.core.quick.PivotLocation;
import sort.core.quick.SequentialPartitioner;
import tnx.lab.rubric.TnXRubric;

/**
 * @author Finn Voichick
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link XQuicksort#parallelQuicksort(java.util.concurrent.ExecutorService, int[], int, sort.core.quick.Partitioner)}
 */
@RunWith(Parameterized.class)
@TnXRubric(TnXRubric.Category.EXECUTOR_QUICKSORT)
public class QuicksortWeaklyConsistentIteratorTest {
	public QuicksortWeaklyConsistentIteratorTest(int ignored) {
	}

	@Test
	public void test() throws InterruptedException, ExecutionException {
		int[] data = RandomDataUtils.createRandomData(64, 100_000L);

		int MAX_TASK_COUNT = data.length * 10;
		ExecutorService executorService = Executors.newFixedThreadPool(MAX_TASK_COUNT);
		BookkeepingExecutorService executor = new BookkeepingExecutorService.Builder(executorService, MAX_TASK_COUNT, 0)
				.build();
		try {
			XQuicksort.parallelQuicksort(executor, data, 3,
					new SequentialPartitioner(PivotInitialIndexSelector.RANDOM) {
						@Override
						public PivotLocation partitionRange(int[] data, int min, int maxExclusive) {
							if (ThreadLocalRandom.current().nextInt(5) == 0) {
								SleepUtils.sleep(ThreadLocalRandom.current().nextLong(500L));
							}
							return super.partitionRange(data, min, maxExclusive);
						}
					});
			assertEquals("All futures not completed", 0, executor.getNotYetJoinedTaskCount());
		} finally {
			executorService.shutdown();
		}

	}

	@Parameters(name = "iteration: {0}")
	public static Collection<Object[]> getConstructorArguments() {
		List<Object[]> result = new LinkedList<>();
		for (int i = 0; i < 16; ++i) {
			result.add(new Object[] { i });
		}
		return result;
	}
}

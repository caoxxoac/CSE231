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
package matrixmultiply.studio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import org.junit.Test;

import edu.wustl.cse231s.v5.api.Bookkeeping;
import edu.wustl.cse231s.v5.bookkeep.BookkeepingUtils;
import matrixmultiply.core.MatrixMultiplier;
import matrixmultiply.core.MatrixMultiplyTestUtils;
import matrixmultiply.core.MatrixUtils;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class MatrixMultiplyParallelTest {
	private static double[][] createRandom(int size) {
		double[][] m = new double[size][size];
		MatrixUtils.setAllRandom(m);
		return m;
	}

	private static void test(int size, MatrixMultiplier matrixMultiplier, Consumer<Bookkeeping> bookkeepingConsumer) {
		double[][] a = createRandom(size);
		double[][] b = createRandom(size);
		double[][] expected = MatrixMultiplyTestUtils.multiply(a, b);
		BookkeepingUtils.bookkeep(() -> {
			double[][] actual = matrixMultiplier.multiply(a, b);
			assertTrue(Arrays.deepEquals(expected, actual));
		}, bookkeepingConsumer);
	}

	@Test
	public void testForallForall() throws InterruptedException, ExecutionException {
		int SIZE = 16;
		test(SIZE, new ForallForallMatrixMultiplier(), (bookkeep) -> {
			int forasyncCount = bookkeep.getForasyncTotalInvocationCount();
			int forasync2dCount = bookkeep.getForasync2dInvocationCount();
			int finishCount = bookkeep.getNonAccumulatorFinishInvocationCount();
			assertNotEquals(0, forasyncCount);
			assertNotEquals(1, forasyncCount);
			assertEquals(SIZE + 1, forasyncCount);

			assertNotEquals(0, finishCount);
			assertNotEquals(1, finishCount);
			assertEquals(SIZE + 1, finishCount);

			assertEquals(0, forasync2dCount);
		});
	}

	@Test
	public void testForall2d() throws InterruptedException, ExecutionException {
		int SIZE = 16;
		test(SIZE, new Forall2dMatrixMultiplier(), (bookkeep) -> {
			int forasyncCount = bookkeep.getForasyncTotalInvocationCount();
			int forasync2dCount = bookkeep.getForasync2dInvocationCount();
			int forasync2dChunkedCount = bookkeep.getForasync2dChunkedInvocationCount();
			int finishCount = bookkeep.getNonAccumulatorFinishInvocationCount();
			assertEquals(0, forasyncCount);
			assertEquals(0, forasync2dChunkedCount);
			assertEquals(1, forasync2dCount);
			assertEquals(1, finishCount);
		});
	}

	@Test
	public void testForall2dChunked() throws InterruptedException, ExecutionException {
		int SIZE = 16;
		test(SIZE, new Forall2dChunkedMatrixMultiplier(), (bookkeep) -> {
			int forasyncCount = bookkeep.getForasyncTotalInvocationCount();
			int forasync2dCount = bookkeep.getForasync2dInvocationCount();
			int forasync2dChunkedCount = bookkeep.getForasync2dChunkedInvocationCount();
			int finishCount = bookkeep.getNonAccumulatorFinishInvocationCount();
			assertEquals(0, forasyncCount);
			assertEquals(0, forasync2dCount);
			assertEquals(1, forasync2dChunkedCount);
			assertEquals(1, finishCount);
		});
	}
}

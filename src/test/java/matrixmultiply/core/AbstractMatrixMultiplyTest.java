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
package matrixmultiply.core;

import static edu.wustl.cse231s.v5.V5.launchApp;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
@RunWith(Parameterized.class)
public abstract class AbstractMatrixMultiplyTest {
	private final MatrixMultiplier matrixMultiplier;
	private final int size;

	public AbstractMatrixMultiplyTest(MatrixMultiplier matrixMultiplier, int size) {
		this.matrixMultiplier = matrixMultiplier;
		this.size = size;
	}

	@Test
	public void testRandomlyFilled() throws InterruptedException, ExecutionException {
		int A_ROW_COUNT = this.size;
		int B_COL_COUNT = this.size;
		int A_COL_AND_B_ROW_COUNT = this.size;
		double[][] originalA = new double[A_ROW_COUNT][A_COL_AND_B_ROW_COUNT];
		double[][] originalB = new double[A_COL_AND_B_ROW_COUNT][B_COL_COUNT];
		MatrixUtils.setAllRandom(originalA);
		MatrixUtils.setAllRandom(originalB);
		double[][] a = MatrixUtils.copy(originalA);
		assertTrue(Arrays.deepEquals(originalA, a));
		double[][] b = MatrixUtils.copy(originalB);
		assertTrue(Arrays.deepEquals(originalB, b));
		launchApp(() -> {
			double[][] expected = MatrixMultiplyTestUtils.multiply(a, b);
			double[][] actual = this.matrixMultiplier.multiply(a, b);
			assertTrue("do not mutate parameter a", Arrays.deepEquals(originalA, a));
			assertTrue("do not mutate parameter b", Arrays.deepEquals(originalB, b));
			assertTrue("incorrect result", Arrays.deepEquals(expected, actual));
		});
	}

	@Test
	public void testIdentity() throws InterruptedException, ExecutionException {
		int A_ROW_COUNT = this.size;
		int B_COL_COUNT = this.size;
		int A_COL_AND_B_ROW_COUNT = this.size;
		double[][] a = new double[A_ROW_COUNT][A_COL_AND_B_ROW_COUNT];
		double[][] b = new double[A_COL_AND_B_ROW_COUNT][B_COL_COUNT];
		MatrixUtils.setIdentity(a);
		MatrixUtils.setIdentity(b);
		assertTrue(MatrixUtils.isIdentity(a));
		assertTrue(MatrixUtils.isIdentity(b));
		double[][] expected = MatrixMultiplyTestUtils.multiply(a, b);
		assertTrue(MatrixUtils.isIdentity(expected));
		launchApp(() -> {
			double[][] actual = this.matrixMultiplier.multiply(a, b);
			assertTrue("incorrect result", Arrays.deepEquals(expected, actual));
		});
	}
}

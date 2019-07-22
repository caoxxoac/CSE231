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
package pack.studio;

import static edu.wustl.cse231s.v5.V5.forall;

import java.lang.reflect.Array;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import edu.wustl.cse231s.NotYetImplementedException;
import scan.studio.StepEfficientParallelScan;

/**
 * @author Xiangzhi Cao
 * @author Will Zhao
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class ParallelPack {
	public static <T> T[] pack(Class<T[]> arrayType, T[] arr, Predicate<T> predicate)
			throws InterruptedException, ExecutionException {
		int length = arr.length;
		if (length <= 0) {
			return arr;
		}
		int[] flag = new int[length];
		forall(0, length, (i)->{
			if (predicate.test(arr[i])) {
				flag[i] = 1;
			}
			else {
				flag[i] = 0;
			}
		});
		int[] prefixSum = StepEfficientParallelScan.sumScanInclusive(flag);
		int newLength = prefixSum[prefixSum.length-1];

		T[] result = createArray(arrayType, newLength);
		
		forall(0, length, (i)->{
			if (isChangedFromNeighborOnTheLeft(prefixSum, i)) {
				int index = prefixSum[i] - 1;
				result[index] = arr[i];
			}
		});
		
		return result;
	}

	/**
	 * 
	 * @param arrayType
	 *            Desired type of the array.
	 * @param length
	 *            Desired length of the array.
	 * @return An array of desired length and type.
	 */
	private static <T> T[] createArray(Class<T[]> arrayType, int length) {
		return arrayType.cast(Array.newInstance(arrayType.getComponentType(), length));
	}

	private static boolean isChangedFromNeighborOnTheLeft(int[] prefixSum, int index) {
		if (index > 0) {
			return prefixSum[index - 1] < prefixSum[index];
		} else {
			return prefixSum[0] == 1;
		}
	}

}

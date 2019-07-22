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

package scan.studio;

import static edu.wustl.cse231s.v5.V5.forall;

import java.util.concurrent.ExecutionException;

import edu.wustl.cse231s.NotYetImplementedException;
import edu.wustl.cse231s.array.SwappableIntArrays;

/**
 * @author Xiangzhi Cao
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class StepEfficientParallelScan {
	public static int[] sumScanInclusive(SwappableIntArrays swappable) throws InterruptedException, ExecutionException {
		int[] nums = swappable.getSrc();
		int length = nums.length;
		int power = 0;
		int num = 1;
		while (num < length) {
			int temp = num;
			forall(0, nums.length, i->{
				int current = swappable.getSrc()[i];
				if (i < temp) {
					swappable.getDst()[i] = current;
				}
				else {
					swappable.getDst()[i] = current + swappable.getSrc()[i-temp];
				}
			});
			swappable.swap();
			power ++;
			num = (int)Math.pow(2, power);

		}
		return swappable.getSrc();
	}

	public static int[] sumScanInclusive(int[] original) throws InterruptedException, ExecutionException {
		return sumScanInclusive(new SwappableIntArrays(1, original));
	}
}
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

package scan.challenge;

import static edu.wustl.cse231s.v5.V5.async;
import static edu.wustl.cse231s.v5.V5.finish;
import static edu.wustl.cse231s.v5.V5.forall;

import java.util.concurrent.ExecutionException;

import edu.wustl.cse231s.NotYetImplementedException;
import edu.wustl.cse231s.array.SwappableIntArrays;

/**
 * @author Xiangzhi Cao
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class WorkEfficientParallelScan {
	public static void sumScanExclusiveInPlace(int[] data) throws InterruptedException, ExecutionException {
		int dLength = data.length;
		int num = dLength;
		int power = 0;
		for (int i=dLength-1; i>0; i--) {
			data[i] = data[i-1];
		}
		data[0] = 0;

		while (num > 1) {
			int temp = (int) Math.pow(2, power);
			power ++;
			int temp2 = (int) Math.pow(2, power);
			num = dLength / temp2;

			forall(1, num+1, i->{
				data[i*temp2-1] = data[i*temp2-temp-1] + data[i*temp2-1];
			});
		}
		power --;

		while (power > 0) {
			int temp = (int) Math.pow(2, power);
			power --;
			int temp2 = (int) Math.pow(2, power);

			num = dLength / temp;
			forall(1, num, i->{
				if ((i * temp + temp2) < dLength) {
					data[i*temp+temp2-1] = data[i*temp-1] + data[i*temp+temp2-1];
				}
			});
		}
	}

}

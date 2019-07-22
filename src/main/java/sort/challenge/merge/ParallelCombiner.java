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

package sort.challenge.merge;

import static edu.wustl.cse231s.v5.V5.async;
import static edu.wustl.cse231s.v5.V5.finish;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import edu.wustl.cse231s.NotYetImplementedException;
import sort.core.merge.Combiner;

/**
 * @author Xiangzhi Cao
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class ParallelCombiner implements Combiner {
	private final int[] buffer;
	private final int threshold;

	public ParallelCombiner(int bufferLength, int threshold) {
		this.buffer = new int[bufferLength];
		this.threshold = threshold;
	}

	private void sequentialCombine(int bufferIndex, int[] data, int aMin, int aMaxExclusive, int bMin,
			int bMaxExclusive) {
		int indexA = aMin;
		int indexB = bMin;
		while (indexA < aMaxExclusive && indexB < bMaxExclusive) {
			this.buffer[bufferIndex++] = (data[indexA] < data[indexB]) ? data[indexA++] : data[indexB++];
		}
		while (indexA < aMaxExclusive) {
			this.buffer[bufferIndex++] = data[indexA++];
		}
		while (indexB < bMaxExclusive) {
			this.buffer[bufferIndex++] = data[indexB++];
		}
	}

	private void parallelCombine(int bufferIndex, int[] data, int aMin, int aMaxExclusive, int bMin, int bMaxExclusive)
			throws InterruptedException, ExecutionException {
//		finish(()->{
//			async(()->{
//				int count = 0;
//				for (int i=aMin; i<aMaxExclusive; i++) {
//					while (count < bMaxExclusive - bMin) {
//						if (data[i] >= data[indexB+count]) {
//							count ++;
//						}
//						else {
//							this.buffer[count+bufferIndex+i-aMin] = data[i];
//							break;
//						}
//					}
//					if (count == bMaxExclusive - bMin) {
//						this.buffer[count+bufferIndex+bMaxExclusive-bMin] = data[i];
//						count ++;
//					}
//				}
//			});
//			int count2 = 0;
//			for (int j=bMin; j<bMaxExclusive; j++) {
//				while (count2 < aMaxExclusive - aMin) {
//					if (data[j] > data[indexA+count2]) {
//						count2 ++;
//					}
//					else {
//						this.buffer[count2+bufferIndex+j-bMin] = data[j];
//						break;
//					}
//				}
//				if (count2 == aMaxExclusive - aMin) {
//					this.buffer[count2+bufferIndex+aMaxExclusive-aMin] = data[j];
//					count2 ++; 
//				}
//			}
//		});
		if (aMaxExclusive - aMin <= this.threshold || bMaxExclusive - bMin <= this.threshold) {
			sequentialCombine(bufferIndex, data, aMin, aMaxExclusive, bMin, bMaxExclusive);
		}
		else {
			int aMid = (aMin+aMaxExclusive) / 2;
			int[] b = new int[1];
			int bMid = Arrays.binarySearch(data, bMin, bMaxExclusive, data[aMid]);
			if (bMid < 0) {
				b[0] = - bMid - 1;
			}
			else {
				b[0] = bMid;
			}
			int length = b[0] - bMin + aMid - aMin;
			
			finish(()->{
				async(()->{
					parallelCombine(bufferIndex, data, aMin, aMid, bMin, b[0]);
				});
				parallelCombine(bufferIndex+length, data, aMid, aMaxExclusive, b[0], bMaxExclusive);
			});
		}
	}

	@Override
	public void combineRange(int[] data, int min, int mid, int maxExclusive)
			throws InterruptedException, ExecutionException {
		parallelCombine(min, data, min, mid, mid, maxExclusive);
		System.arraycopy(buffer, min, data, min, maxExclusive - min);
	}
}

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
package tnx.lab.thread;

import java.util.concurrent.ThreadFactory;

import edu.wustl.cse231s.NotYetImplementedException;

/**
 * An parallel array sum implementation that uses Java's {@link Thread} class to
 * sum each half of the array individually.
 * 
 * @author Xiangzhi Cao
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class TAgeSum {

	/**
	 * Should sum an array of ages. You are given an array of ages, and should
	 * calculate the sum of everything in the array. This should be done in
	 * parallel, calculating the sum of the lower half and the upper half
	 * individually, then summing those two partial sums. Like with the first
	 * nucleobase counting assignment, you will need to create an array to store the
	 * sub-sums.
	 * 
	 * @param ages
	 *            an array of people's ages
	 * @param threadFactory
	 *            an object used for creating {@code Thread}s
	 * @return the sum of every age in the array
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 */
	public static int sumUpperLowerSplit(int[] ages, ThreadFactory threadFactory) throws InterruptedException {
		int[] sum = new int[2];
		int mid = ages.length / 2;
		Thread thread = threadFactory.newThread(()->{
			for (int i=0; i<mid; i++) {
				sum[0] += ages[i];
			}
		});
		thread.start();
		
		for (int i=mid; i<ages.length; i++) {
			sum[1] += ages[i];
		}
		
		thread.join();
		
		return sum[0] + sum[1];
	}

}

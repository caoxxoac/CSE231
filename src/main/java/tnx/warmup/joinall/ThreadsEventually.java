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
package tnx.warmup.joinall;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.wustl.cse231s.sleep.SleepUtils;

/**
 * @author Xiangzhi Cao
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class ThreadsEventually {
	private static void startThread(Queue<Thread> queue, int i, int maxExclusive) {
		if (i < maxExclusive) {
			Thread thread = new Thread(() -> {
				SleepUtils.sleep(100);
				startThread(queue, i + 1, maxExclusive);
			});
			thread.start();
			queue.offer(thread);
		}
	}

	private static void fillQueueWithStartedThreadsEventually(Queue<Thread> queue, int truthAndBeautyCount) {
		startThread(queue, 0, truthAndBeautyCount);
	}

	/*
	 * @see java.util.Collection#isEmpty()
	 * 
	 * @see java.util.Queue#poll()
	 */
	private static int joinAllInQueueViaPoll(Queue<Thread> queue) throws InterruptedException {
		System.err.println("TODO: fix broken implementation of joinAllInQueueViaPoll");
		int notLikelyToBeCorrectCount = ThreadsRightNow.joinAllInQueueViaIteration(queue);
		return notLikelyToBeCorrectCount;
	}

	public static int startAndJoin_brokenInitially_requiresFixing(int truthAndBeautyCount) throws InterruptedException {
		Queue<Thread> queue = new ConcurrentLinkedQueue<>();
		fillQueueWithStartedThreadsEventually(queue, truthAndBeautyCount);
		return joinAllInQueueViaPoll(queue);
	}
}

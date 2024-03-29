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
package lock.allornothing.util.studio;

import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

import edu.wustl.cse231s.NotYetImplementedException;
import edu.wustl.cse231s.concurrent.Interruptible;

/**
 * @author Xiangzhi Cao
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class LockUtils {
	/**
	 * Attempts to lock all of the specified locks. If any of the locks fail to be
	 * acquired this method must unlock all of the previous locks this invocation
	 * acquired.
	 */
	private static boolean tryLockAll(List<ReentrantLock> locks) {
		ListIterator<ReentrantLock> lks = locks.listIterator();
		while (lks.hasNext()) {
			ReentrantLock next = lks.next();
			if (!next.tryLock()) {
				next = lks.previous();
				
				// unlock previous
				while (lks.hasPrevious()) {
					next = lks.previous();
					if (next.isLocked()) {
						next.unlock();
					}
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * Unlocks all of the specified locks. If a lock is not held by the current
	 * thread then an IllegalMonitorStateException is thrown.
	 * 
	 * @throws IllegalMonitorStateException
	 */
	private static void unlockAll(List<ReentrantLock> locks) {
		for (ReentrantLock lock : locks) {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
			else {
				throw new IllegalMonitorStateException();
			}
		}
	}

	/**
	 * Runs the specified action if and only if all of the specified locks can be
	 * acquired.
	 * 
	 * @return whether or not successful
	 */
	public static boolean runWithAllLocksOrDontRunAtAll(List<ReentrantLock> locks, Runnable body) {
		boolean allLock = tryLockAll(locks);
		if (allLock) {
			try {
				body.run();
			}
			finally {
				unlockAll(locks);
			}
		}
		return allLock;
	}

	/**
	 * Repeatedly attempts to run the specified body of code until it succeeds with
	 * the specified failure to acquire all locks action.
	 */
	public static void runWithAllLocks(List<ReentrantLock> locks, Runnable body, Interruptible failureAction)
			throws InterruptedException {
		while (true) {
			if (runWithAllLocksOrDontRunAtAll(locks, body)) {
				break;
			} else {
				failureAction.run();
			}
		}
	}

	/**
	 * Repeatedly attempts to run the specified body of code until it succeeds with
	 * default failure to acquire all locks action.
	 */
	public static void runWithAllLocks(List<ReentrantLock> locks, Runnable body) throws InterruptedException {
		runWithAllLocks(locks, body, () -> {
			if (Thread.currentThread().isInterrupted()) {
				throw new InterruptedException();
			}
			Thread.yield();
		});
	}
}

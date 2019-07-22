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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runners.MethodSorters;

import edu.wustl.cse231s.junit.JUnitUtils;

/**
 * @author Miles Cushing (mcushing@wustl.edu)
 * 
 *         {@link LockUtils#tryLockAll(List)} {@link LockUtils#unlockAll(List)}
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TryLockAll_UnlockAllHeldLocksTest {
	@Rule
	public TestRule timeout = JUnitUtils.createTimeoutRule(4);

	/**
	 * Test A checks {@link LockUtils#tryLockAll(List)}. It creates a lock and sees
	 * if the method will successfully lock it, followed by sending the now locked
	 * lock back through and looking for 'false' to be returned
	 * 
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	@Test
	public void testA_LockSingleLock() throws InterruptedException, ExecutionException {
		ReentrantLock uncontendedLock = new ReentrantLock();
		List<ReentrantLock> locks = Arrays.asList(uncontendedLock);
		for (ReentrantLock lock : locks) {
			assertFalse(lock.isLocked());
		}
		Mutable<Boolean> isSuccessful = new MutableObject<>(null);
		startAndJoinThread(() -> {
			isSuccessful.setValue(AccessLockUtils.tryLockAll(locks));
			for (ReentrantLock lock : locks) {
				assertTrue(lock.isHeldByCurrentThread());
			}
		});
		assertNotNull(isSuccessful.getValue());
		assertTrue("tryLockAll did not successfully lock the lock and return true",
				isSuccessful.getValue().booleanValue());
		for (ReentrantLock lock : locks) {
			assertTrue(lock.isLocked());
		}
		startAndJoinThread(() -> {
			isSuccessful.setValue(AccessLockUtils.tryLockAll(locks));
			for (ReentrantLock lock : locks) {
				assertFalse(lock.isHeldByCurrentThread());
			}
		});
		assertNotNull(isSuccessful.getValue());
		assertFalse("tryLockAll failed to return false when given already held lock",
				isSuccessful.getValue().booleanValue());
	}

	/**
	 * Test B checks {@link LockUtils#tryLockAll(List)}. It creates 3 locks and sees
	 * if the method will successfully lock them all, followed by sending the now
	 * locked locks back through and looking for 'false' to be returned
	 * 
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	@Test
	public void testB_LockMultipleLocks() throws InterruptedException, ExecutionException {
		List<ReentrantLock> locks = new LinkedList<>();
		for (int i = 0; i < 3; i++) {
			locks.add(new ReentrantLock());
		}
		for (ReentrantLock lock : locks) {
			assertFalse(lock.isLocked());
		}
		Mutable<Boolean> isSuccessful = new MutableObject<>(null);
		startAndJoinThread(() -> {
			isSuccessful.setValue(AccessLockUtils.tryLockAll(locks));
			for (ReentrantLock lock : locks) {
				assertTrue(lock.isHeldByCurrentThread());
			}
		});
		assertNotNull(isSuccessful.getValue());
		assertTrue("tryLockAll did not successfully lock all the locks and return true",
				isSuccessful.getValue().booleanValue());
		for (ReentrantLock lock : locks) {
			assertTrue(lock.isLocked());
		}
		startAndJoinThread(() -> {
			isSuccessful.setValue(AccessLockUtils.tryLockAll(locks));
			for (ReentrantLock lock : locks) {
				assertFalse(lock.isHeldByCurrentThread());
			}
		});
		assertNotNull(isSuccessful.getValue());
		assertFalse("tryLockAll failed to return false when given already held locks",
				isSuccessful.getValue().booleanValue());
	}

	/**
	 * Test C checks {@link LockUtils#tryLockAll(List)}. It creates 3 locks and
	 * locks one of them in this thread. Then in a new thread, it calls the
	 * student's method, which should return 'false'
	 * 
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	@Test
	public void testC_SingleHeldOfMultipleLocks() throws InterruptedException, ExecutionException {
		List<ReentrantLock> locks = new LinkedList<>();
		for (int i = 0; i < 3; i++) {
			locks.add(new ReentrantLock());
		}

		for (int i = 0; i < locks.size(); i++) {
			for (ReentrantLock lock : locks) {
				assertFalse(lock.isLocked());
			}

			ReentrantLock heldLock = locks.get(i);
			heldLock.lock();

			assertTrue(heldLock.isHeldByCurrentThread());

			Mutable<Boolean> isSuccessful = new MutableObject<>(null);
			startAndJoinThread(() -> {
				isSuccessful.setValue(AccessLockUtils.tryLockAll(locks));
				for (ReentrantLock lock : locks) {
					assertFalse(lock.isHeldByCurrentThread());
				}
			});
			assertNotNull(isSuccessful.getValue());
			assertFalse("tryLockAll failed to return false when given already held locks",
					isSuccessful.getValue().booleanValue());

			assertTrue(heldLock.isHeldByCurrentThread());
			heldLock.unlock();

			for (ReentrantLock lock : locks) {
				assertFalse(lock.isLocked());
			}
		}
	}

	/**
	 * Test D checks {@link LockUtils#tryLockAll(List)}. It creates a 7 locks and
	 * locks the 4th one in the test thread. Then it spawns a new thread. In this
	 * thread it locks lock 6 then calls tryLockAll(). The method should return
	 * 'false' and unlock locks 1-3, but NOT lock 6, since that was previously
	 * locked by someone else.
	 * 
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	@Test
	public void testD_OnlyUnlockingImmediatelyPreviouslyAcquiredLocks()
			throws InterruptedException, ExecutionException {
		List<ReentrantLock> locks = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			locks.add(new ReentrantLock());
		}

		// acquiring this lock will prevent the runWithAllLocksOrDontRunAtAll thread
		// from succeeding
		Lock lock = locks.get(3);
		lock.lock();
		MutableObject<Boolean> isSuccessful = new MutableObject<>();
		startAndJoinThread(() -> {
			// since lock 5 will come after 3, it should not be locked by
			// runWithAllLocksOrDontRunAtAll so it should not be unlocked.
			int indexOfLock = 5;
			locks.get(indexOfLock).lock();
			isSuccessful.setValue(AccessLockUtils.tryLockAll(locks));
			assertTrue("Only unlock the locks you successfully acquired.  Do NOT invoke unlockAll.",
					locks.get(indexOfLock).isHeldByCurrentThread());
		});
		assertNotNull(isSuccessful.getValue());
		assertFalse(isSuccessful.getValue());
	}

	/**
	 * Test E checks {@link LockUtils#unlockAll(List)}. It creates a list of three
	 * locks. Then in a new thread, locks them all and calls the student's method.
	 * Then checks if all the locks were successfully unlocked.
	 * 
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	@Test
	public void testE_UnlockingAllLocks() throws InterruptedException, ExecutionException {
		List<ReentrantLock> locks = new LinkedList<>();
		for (int i = 0; i < 3; i++) {
			locks.add(new ReentrantLock());
		}
		for (ReentrantLock lock : locks) {
			assertFalse(lock.isLocked());
		}
		startAndJoinThread(() -> {
			for (ReentrantLock lock : locks) {
				lock.lock();
				assertTrue(lock.isHeldByCurrentThread());
			}
			AccessLockUtils.unlockAll(locks);
		});
		for (ReentrantLock lock : locks) {
			assertFalse("unlockAllHeldLocks did not unlock all locks", lock.isLocked());
		}
	}

	/**
	 * Test F checks {@link LockUtils#unlockAll(List)}. It creates a list of three
	 * locks. The main thread locks the first lock in the list then calls the
	 * student's method. Since all of the locks are not held by the current thread
	 * and IllegalMonitorStateException should be thrown.
	 */
	@Test(expected = IllegalMonitorStateException.class)
	public void testF_UnlockingSomeHeldLocks() {
		List<ReentrantLock> locks = new LinkedList<>();
		for (int i = 0; i < 3; i++) {
			locks.add(new ReentrantLock());
		}
		for (ReentrantLock lock : locks) {
			assertFalse(lock.isLocked());
		}
		ReentrantLock heldLock = locks.get(0);
		heldLock.lock();

		AccessLockUtils.unlockAll(locks); // should throw IllegalMonitorStateException
	}

	/**
	 * Test G checks {@link LockUtils#unlockAll(List)}. It creates a list of three
	 * locks. The current thread locks the first lock in the list then locks the
	 * rest in a different thread. calls the student's method. Since all of the
	 * locks are not held by the current thread and IllegalMonitorStateException
	 * should be thrown.
	 * 
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	@Test(expected = IllegalMonitorStateException.class)
	public void testF_UnlockingSomeHeldByCurrentThreadLocks() throws InterruptedException, ExecutionException {
		List<ReentrantLock> locks = new LinkedList<>();
		for (int i = 0; i < 3; i++) {
			locks.add(new ReentrantLock());
		}
		for (ReentrantLock lock : locks) {
			assertFalse(lock.isLocked());
		}
		ReentrantLock heldLock = locks.get(0);
		heldLock.lock();

		startAndJoinThread(() -> {
			for (int i = 1; i < 3; i++) {
				ReentrantLock tempLock = locks.get(i);
				tempLock.lock();
				assertTrue(tempLock.isHeldByCurrentThread());
			}
			// note: these locks are not unlocked
		});

		AccessLockUtils.unlockAll(locks); // should throw IllegalMonitorStateException
	}

	private static Thread startAndJoinThread(Runnable body) throws InterruptedException, ExecutionException {
		Thread thread = new Thread(body);
		MutableObject<Throwable> throwableReference = new MutableObject<>();
		thread.setUncaughtExceptionHandler((_thread, _throwable) -> {
			throwableReference.setValue(_throwable);
		});
		thread.start();
		thread.join();
		Throwable throwableValue = throwableReference.getValue();
		if (throwableValue != null) {
			throw new ExecutionException(throwableValue);
		} else {
			return thread;
		}
	}
}

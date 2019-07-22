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
package lock.order.studio;

import static edu.wustl.cse231s.v5.V5.async;
import static edu.wustl.cse231s.v5.V5.finish;
import static edu.wustl.cse231s.v5.V5.launchApp;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutionException;

import org.junit.Test;

import lock.order.studio.BankAccountLockOrdering;
import locking.core.banking.DefaultAccount;
import locking.core.banking.TransferResult;

/**
 * @author Ben Choi (benjaminchoi@wustl.edu)
 *         {@link BankAccountLockOrdering#transferMoney(locking.core.Account, locking.core.Account, int)}
 */
public class LockOrderingDeadlockTest {

	@Test(timeout = 5000)
	public void test() throws InterruptedException, ExecutionException {
		DefaultAccount[] accounts = new DefaultAccount[2];
		// Make 2 accounts with $500 each
		for (int i = 0; i < 2; ++i) {
			accounts[i] = new DefaultAccount(i, 5000);
		}
		launchApp(() -> {
			finish(() -> {
				// In parallel, attempt numerous transfers between account[0] and account[1] to
				// cause the sender and recipient to be the same person at the same time
				for (int i = 0; i < 1_000; ++i) {
					async(() -> {
						assertEquals("The transfer failed to work asynchronously",
								BankAccountLockOrdering.transferMoney(accounts[0], accounts[1], 2),
								TransferResult.SUCCESS);
						assertEquals("The transfer failed to work asynchronously",
								BankAccountLockOrdering.transferMoney(accounts[1], accounts[0], 1),
								TransferResult.SUCCESS);
					});
				}
			});
		});
		// Make sure the final balances are correct for all the transfers that occur
		// above
		assertEquals("The account balances did not update properly in parallel", 4000, accounts[0].getBalance());
		assertEquals("The account balances did not update properly in parallel", 6000, accounts[1].getBalance());
	}

}

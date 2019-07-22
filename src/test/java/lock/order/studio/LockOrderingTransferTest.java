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
public class LockOrderingTransferTest {

	@Test(timeout = 1000)
	public void test() throws InterruptedException, ExecutionException {
		DefaultAccount[] accounts = new DefaultAccount[20];
		// Make 20 accounts all with $5000
		for (int i = 0; i < 20; ++i) {
			accounts[i] = new DefaultAccount(i, 5000);
		}
		// Attempt transfers for more money than an account has
		for (int i = 0; i < 19; ++i) {
			assertEquals("The transfer should have been ommitted as the sender has insufficient funds to continue",
					TransferResult.INSUFFICIENT_FUNDS,
					BankAccountLockOrdering.transferMoney(accounts[i], accounts[i + 1], 5001));
		}
		// Transfer $50 between every combination of accounts
		for (int i = 0; i < 20; ++i) {
			for (int j = 19; j >= 0; --j) {
				if (i != j) {
					int iBalance = accounts[i].getBalance();
					int jBalance = accounts[j].getBalance();
					assertEquals("The transfer should have been successful, but it was not",
							BankAccountLockOrdering.transferMoney(accounts[i], accounts[j], 50),
							TransferResult.SUCCESS);
					assertEquals("The sender's balance did not update properly", iBalance - 50,
							accounts[i].getBalance());
					assertEquals("The recipient's balance did not update properly", jBalance + 50,
							accounts[j].getBalance());
				} else {
					assertEquals(
							"The transfer should have been ommitted as the sender and recipient are the same account",
							TransferResult.INTRA_ACCOUNT_TRANSFER_OMITTED,
							BankAccountLockOrdering.transferMoney(accounts[i], accounts[j], 50));
				}
			}
		}
		// Make sure all accounts end with $5000
		for (int i = 0; i < 20; ++i) {
			assertEquals("The account balances did not update properly", 5000, accounts[i].getBalance());
		}
	}

}

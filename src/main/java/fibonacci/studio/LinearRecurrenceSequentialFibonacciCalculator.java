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
package fibonacci.studio;

import static edu.wustl.cse231s.v5.V5.doWork;

import java.math.BigInteger;

import edu.wustl.cse231s.NotYetImplementedException;
import edu.wustl.cse231s.asymptoticanalysis.OrderOfGrowth;
import fibonacci.core.FibonacciCalculator;

/**
 * @author Xiangzhi Cao
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class LinearRecurrenceSequentialFibonacciCalculator implements FibonacciCalculator {
	@Override
	public BigInteger fibonacci(int n) {
		if (n <= 1) {
			return BigInteger.valueOf(n);
		}
		if (n % 2 == 1) {
			int k = (n + 1) / 2;
			BigInteger fk = fibonacci(k);
			BigInteger fk1 = fibonacci(k-1);
			return fk.multiply(fk).add(fk1.multiply(fk1));
		}
		else {
			int k = n / 2;
			BigInteger fk = fibonacci(k);
			BigInteger fk1 = fibonacci(k-1);
			return (fk1.multiply(BigInteger.valueOf(2)).add(fk)).multiply(fk);
		}
	}

	@Override
	public OrderOfGrowth getOrderOfGrowth() {
		return OrderOfGrowth.LOGARITHMIC;
	}

	@Override
	public boolean isSusceptibleToStackOverflowError() {
		// technically true
		return false;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}

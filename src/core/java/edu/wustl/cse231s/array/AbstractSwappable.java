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
package edu.wustl.cse231s.array;

import java.util.Objects;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
@ThreadSafe
@Deprecated
public abstract class AbstractSwappable<T> {
	private final int registrations;
	private final T originalData;
	private final T a;
	private final T b;

	@GuardedBy("this")
	private int outstandingSwaps;
	@GuardedBy("this")
	private int phase;

	public AbstractSwappable(int registrations, T originalData, T a, T b) {
		if (registrations <= 0) {
			throw new IllegalArgumentException("registrations must be >= 1; actual: " + registrations);
		}
		Objects.requireNonNull(originalData);
		Objects.requireNonNull(a);
		Objects.requireNonNull(b);

		this.registrations = registrations;
		this.originalData = originalData;
		this.a = a;
		this.b = b;
		this.outstandingSwaps = registrations;
		this.phase = 0;
	}

	private synchronized boolean isEvenPhase() {
		return (phase & 1) == 0;
	}

	public T getSrc() {
		if (phase == 0) {
			return this.originalData;
		} else {
			return isEvenPhase() ? this.b : this.a;
		}
	}

	public T getDst() {
		return isEvenPhase() ? this.a : this.b;
	}

	public synchronized void swap() {
		this.outstandingSwaps--;
		if (this.outstandingSwaps == 0) {
			this.outstandingSwaps = registrations;
			this.phase++;
		}
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[originalData=" + this.originalData.toString() + "; phase=" + this.phase
				+ "]";
	}
}

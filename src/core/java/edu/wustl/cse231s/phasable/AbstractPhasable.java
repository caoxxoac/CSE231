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
package edu.wustl.cse231s.phasable;

import java.util.Objects;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public abstract class AbstractPhasable<T> {
	private final T originalData;
	private final T a;
	private final T b;

	public AbstractPhasable(T originalData, T a, T b) {
		Objects.requireNonNull(originalData);
		Objects.requireNonNull(a);
		Objects.requireNonNull(b);

		this.originalData = originalData;
		this.a = a;
		this.b = b;
	}

	public T getSrcForPhase(int phaseIndex) {
		if (phaseIndex == 0) {
			return this.originalData;
		} else {
			return (phaseIndex & 1) == 0 ? this.b : this.a;
		}
	}

	public T getDstForPhase(int phaseIndex) {
		return (phaseIndex & 1) == 0 ? this.a : this.b;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[originalData=" + this.originalData.toString() + "]";
	}
}

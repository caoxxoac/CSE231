/*******************************************************************************
 * Copyright (C) 2016-2019 Dennis Cosgrove
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
package edu.wustl.cse231s.v5.serial;

import edu.wustl.cse231s.v5.api.FinishAccumulator;
import edu.wustl.cse231s.v5.api.NumberReductionOperator;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
/* package-private */ class SerialIntFinishAccumulator implements FinishAccumulator<Integer> {
	private final NumberReductionOperator operator;
	private int value;

	public SerialIntFinishAccumulator(NumberReductionOperator operator) {
		this.operator = operator;
		this.value = operator.getInitialIntValue();
	}

	@Override
	public Integer get() {
		return this.value;
	}

	@Override
	public void put(Integer value) {
		switch (operator) {
		case SUM:
			this.value += value;
			break;
		case PRODUCT:
			this.value *= value;
			break;
		case MIN:
			this.value = Math.min(this.value, value);
			break;
		case MAX:
			this.value = Math.max(this.value, value);
			break;
		}
	}
}

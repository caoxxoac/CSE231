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
package tnx.lab.rubric;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import edu.wustl.cse231s.rubric.RubricCategory;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TnXRubric {
	public static enum Category implements RubricCategory {
		THREAD_NEW(0.05),
		THREAD_UPPER_LOWER(0.1),

		EXECUTOR_COUNT_2WAY(0.1),
		EXECUTOR_COUNT_NWAY(0.15),
		EXECUTOR_COUNT_DIVIDE_AND_CONQUER(0.15),

		SEQUENTIAL_QUICKSORT(0.1),
		EXECUTOR_QUICKSORT(0.25);

		private final double portion;

		private Category(double portion) {
			this.portion = portion;
		}

		@Override
		public double getPortion() {
			return this.portion;
		}
	}

	Category[] value();
}

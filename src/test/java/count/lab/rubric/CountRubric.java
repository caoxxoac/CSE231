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
package count.lab.rubric;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import edu.wustl.cse231s.rubric.RubricCategory;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CountRubric {
	public static enum Category implements RubricCategory {
		RANGE(0.1),
		SEQUENTIAL(0.05),
		MIDPOINT(0.05),
		LOWER_UPPER_CORRECT(0.1),
		LOWER_UPPER_PARALLEL(0.1),
		NWAY_CORRECT(0.15),
		NWAY_PARALLEL(0.10),
		THRESHOLD(0.05),
		DIVIDE_AND_CONQUER_CORRECT(0.10),
		DIVIDE_AND_CONQUER_PARALLEL(0.10),
		STYLE(0.1);
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

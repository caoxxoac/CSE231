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
package kmer.lab.atomicintegerarray;

import kmer.AbstractKMerPreliminaryTest;
import kmer.core.KMerCount;
import kmer.core.KMerCounter;
import kmer.core.array.IntArrayKMerCount;
import kmer.lab.intarray.IntArrayKMerCounter;
import kmer.lab.rubric.KMerRubric;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link IntArrayKMerCounter#parse(java.util.List, int)}
 */
@KMerRubric(KMerRubric.Category.INT_ARRAY)
public class IntArrayKMerPreliminaryTest extends AbstractKMerPreliminaryTest {
	@Override
	protected KMerCounter createKMerCounter() {
		return new IntArrayKMerCounter();
	}

	@Override
	protected Class<? extends KMerCount> getKMerCountClass() {
		return IntArrayKMerCount.class;
	}
}

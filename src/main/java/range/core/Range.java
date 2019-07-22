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

package range.core;

import java.util.concurrent.ExecutionException;

import edu.wustl.cse231s.v5.api.CheckedIntConsumer;

/**
 * @author Finn Voichick
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class Range {
	private final int indexId;
	private final int minInclusive;
	private final int maxExclusive;

	/**
	 * Creates a new range composed an index id, an inclusive lower bound, and an
	 * exclusive upper bound.
	 * 
	 * @param indexId
	 *            the indexID of the range
	 * @param minInclusive
	 *            the inclusive lower bound
	 * @param maxExclusive
	 *            the exclusive upper bound
	 */
	public Range(int indexId, int minInclusive, int maxExclusive) {
		this.indexId = indexId;
		this.minInclusive = minInclusive;
		this.maxExclusive = maxExclusive;
	}

	/**
	 * Gets the value of the index ID.
	 * 
	 * @return the index ID of the slice
	 */
	public int getIndexId() {
		return this.indexId;
	}

	/**
	 * Gets the inclusive lower bound of the slice.
	 * 
	 * @return inclusive lower bound of the slice
	 */
	public int getMinInclusive() {
		return this.minInclusive;
	}

	/**
	 * Gets the exclusive upper bound of the slice.
	 * 
	 * @return exclusive upper bound of the slice
	 */
	public int getMaxExclusive() {
		return this.maxExclusive;
	}

	/**
	 * Performs the given consumer for each index [min, maxExclusive).
	 * 
	 * @param body
	 *            the computation to be performed for each index
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	public void forEachIndex(CheckedIntConsumer body) throws InterruptedException, ExecutionException {
		for (int i = minInclusive; i < maxExclusive; i++) {
			body.accept(i);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 17;
		result = prime * result + indexId;
		result = prime * result + maxExclusive;
		result = prime * result + minInclusive;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof Range) {
			Range other = (Range) obj;
			if (indexId == other.indexId) {
				if (maxExclusive == other.maxExclusive) {
					if (minInclusive == other.minInclusive) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[indexId=" + indexId + ", minInclusive=" + minInclusive + ", maxExclusive="
				+ maxExclusive + "]";
	}

}

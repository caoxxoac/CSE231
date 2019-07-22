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
package kmer.lab.util;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.IntPredicate;

import edu.wustl.cse231s.NotYetImplementedException;
import kmer.core.KMerUtils;
import slice.core.Slice;
import slice.studio.Slices;

/**
 * @author Xiangzhi Cao
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class ThresholdSlices {

	private ThresholdSlices() {
		throw new AssertionError("This class is not instantiable");
	}

	/**
	 * Should either add the given range to the collection or recursively split the
	 * given range, depending on the sliceThreshold.
	 * 
	 * @param slices
	 *            the collection of slices to add to
	 * @param sequence
	 *            a sequence of nucleobases
	 * @param min
	 *            the minimum of the range to look at
	 * @param max
	 *            the maximum of the range to look at
	 * @param slicePredicate
	 *            predicate which if returns true when tested with the range length
	 *            from min to max indicates that continued slicing is appropriate
	 */
	private static void addToCollectionKernel(Collection<Slice<byte[]>> slices, byte[] sequence, int min, int max,
			IntPredicate slicePredicate) { 
		if (slicePredicate.negate().test(max-min)) {
			Slice<byte[]> slice = new Slice<byte[]>(sequence, -1, min, max);
			slices.add(slice);
		}
		else {
			int mid = (min + max) / 2;
			addToCollectionKernel(slices, sequence, min, mid, slicePredicate);
			addToCollectionKernel(slices, sequence, mid, max, slicePredicate);
		}
	}

	/**
	 * Should create a list of slices from the given list of sequences. Each
	 * sequence may be divided many times or not divided at all, depending on its
	 * size relative to the sliceThreshold.
	 * 
	 * @param sequences
	 *            a list of nucleobase sequences
	 * @param k
	 *            the k in k-mer
	 * @param slicePredicate
	 *            predicate which if returns true when tested with the range length
	 *            from min to max indicates that continued slicing is appropriate
	 * @return an unmodifiable list of slices
	 */
	public static List<Slice<byte[]>> createSlices(List<byte[]> sequences, int k, IntPredicate slicePredicate) {
		List<Slice<byte[]>> list = new LinkedList<>();
		int length = sequences.size();
		
		for (int i=0; i<length; i++) {
			byte[] sequence = sequences.get(i);
			int sequenceLength = sequence.length;
			int kmer = sequenceLength - k + 1;
			addToCollectionKernel(list, sequence, 0, kmer, slicePredicate);
		}
		return list;
	}

	/**
	 * Calculate a reasonable threshold to be used with
	 * {@link ThresholdSlices#createSlicesBelowThreshold(List, int, IntPredicate)}
	 * where reasonable is defined to mean will create between 2X and 10X tasks per
	 * available processor.
	 * 
	 * @see Runtime#availableProcessors()
	 * 
	 * @param sequences
	 *            a list of nucleobase sequences
	 * @param k
	 *            the k in k-mer
	 * @return a reasonable threshold
	 */
	public static int calculateReasonableThreshold(List<byte[]> sequences, int k) {
		int avgLength = 0;
		if (sequences.size() ==  0) {
			return 0;
		}
		for (byte[] sequence : sequences) {
			avgLength += sequence.length;
		}
		int threshold = avgLength / sequences.size();
		
		return threshold;
	}

	public static IntPredicate createAboveThresholdPredicate(int threshold) {
		return (int rangeLength) -> {
			return rangeLength > threshold;
		};
	}

	private static IntPredicate createAboveReasonableThresholdPredicate(List<byte[]> sequences, int k) {
		int reasonableThreshold = calculateReasonableThreshold(sequences, k);
		return (int rangeLength) -> {
			return rangeLength > reasonableThreshold;
		};
	}

	public static List<Slice<byte[]>> createReasonableSlices(List<byte[]> sequences, int k) {
		return createSlices(sequences, k, createAboveReasonableThresholdPredicate(sequences, k));
	}
}

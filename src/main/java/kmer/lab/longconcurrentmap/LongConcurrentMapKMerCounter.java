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
package kmer.lab.longconcurrentmap;

import static edu.wustl.cse231s.v5.V5.forall;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;

import com.google.common.base.Predicate;

import edu.wustl.cse231s.NotYetImplementedException;
import kmer.core.KMerCount;
import kmer.core.KMerCounter;
import kmer.core.KMerUtils;
import kmer.core.codecs.LongKMerCodec;
import kmer.core.map.MapKMerCount;
import kmer.lab.util.ThresholdSlices;
import slice.core.Slice;

/**
 * A parallel implementation of {@link KMerCounter} that uses a
 * {@link ConcurrentHashMap}, where each k-mer is represented as a Long.
 * 
 * @author Xiangzhi Cao
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class LongConcurrentMapKMerCounter implements KMerCounter {
	private final IntFunction<ConcurrentMap<Long, Integer>> concurrentMapFactory;
	
	public LongConcurrentMapKMerCounter(IntFunction<ConcurrentMap<Long, Integer>> concurrentMapFactory) {
		this.concurrentMapFactory = concurrentMapFactory;
	}

	@Override
	public KMerCount parse(List<byte[]> sequences, int k) throws InterruptedException, ExecutionException {
		List<Slice<byte[]>> slices = ThresholdSlices.createReasonableSlices(sequences, k);
		int allPossible = KMerUtils.calculateSumOfAllKMers(sequences, k);
		long allPossible2 = KMerUtils.calculatePossibleKMers(k);
		Map<Long, Integer> map;
		if (allPossible > allPossible2) {
			map = this.concurrentMapFactory.apply((int) allPossible2);
		}
		else {
			map = this.concurrentMapFactory.apply(allPossible);
		}
		
		forall(slices, (slice)->{
			int min = slice.getMinInclusive();
			int max = slice.getMaxExclusive();
			byte[] sequence = slice.getOriginalUnslicedData();
			
			for (int index=min; index<max; index++) {
				Long num = KMerUtils.toPackedLong(sequence, index, k);
				map.compute(num, (key, value)->{
					if (value != null) {
						return value + 1;
					}
					return 1;
				});
			}
		});
		
		return new MapKMerCount<>(k, map, LongKMerCodec.INSTANCE);
	}

}

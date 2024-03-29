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
package kmer.lab;

import static edu.wustl.cse231s.v5.V5.launchApp;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Rule;
import org.junit.rules.TestRule;

import edu.wustl.cse231s.junit.JUnitUtils;
import edu.wustl.cse231s.print.AbstractNoPrintingTest;
import kmer.core.KMerCounter;
import kmer.lab.atomicintegerarray.AtomicIntegerArrayKMerCounter;
import kmer.lab.concurrentbuckethashmap.ConcurrentBucketHashMap;
import kmer.lab.intarray.IntArrayKMerCounter;
import kmer.lab.longconcurrentmap.LongConcurrentMapKMerCounter;
import kmer.lab.rubric.KMerRubric;
import kmer.util.KMerResource;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
@KMerRubric(KMerRubric.Category.NO_PRINTING)
public class NoPrintingTest extends AbstractNoPrintingTest {
	@Rule
	public TestRule timeout = JUnitUtils.createTimeoutRule();

	@Override
	protected void testKernel() {
		List<byte[]> sequences = KMerResource.CHOLERAE_ORI_C.getSubSequences();
		KMerCounter[] counters = { new IntArrayKMerCounter(), new AtomicIntegerArrayKMerCounter(),
				new LongConcurrentMapKMerCounter((int initialCapacity) -> {
					return new ConcurrentHashMap<>(initialCapacity);
				}), new LongConcurrentMapKMerCounter((int initialCapacity) -> {
					return new ConcurrentBucketHashMap<>(initialCapacity);
				}) };
		for (KMerCounter counter : counters) {
			launchApp(() -> {
				counter.parse(sequences, 5);
			});
		}
	}
}

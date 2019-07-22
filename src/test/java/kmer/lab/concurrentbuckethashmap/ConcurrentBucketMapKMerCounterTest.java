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
package kmer.lab.concurrentbuckethashmap;

import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import kmer.AbstractKMerCounterTest;
import kmer.core.KMerCounter;
import kmer.lab.longconcurrentmap.LongConcurrentMapKMerCounter;
import kmer.lab.rubric.KMerRubric;
import kmer.util.KMerResource;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link ConcurrentBucketHashMapKMerCounter#parse(java.util.List, int)}
 */
@KMerRubric(KMerRubric.Category.BUCKET_COUNTER_CORRECTNESS)
@RunWith(Parameterized.class)
public class ConcurrentBucketMapKMerCounterTest extends AbstractKMerCounterTest {
	private final boolean isRequestedInitialCapacityPassedAlong;

	public ConcurrentBucketMapKMerCounterTest(KMerResource resource, int k, CheckEntent checkEntent,
			boolean isRequestedInitialCapacityPassedAlong) {
		super(resource, k, checkEntent);
		this.isRequestedInitialCapacityPassedAlong = isRequestedInitialCapacityPassedAlong;
	}

	@Override
	protected KMerCounter createKMerCounter() {
		return new LongConcurrentMapKMerCounter((int requestedInitialCapacity) -> {
			int initialCapacity;
			if (isRequestedInitialCapacityPassedAlong) {
				initialCapacity = requestedInitialCapacity;
			} else {
				initialCapacity = 1024;
			}
			return new ConcurrentBucketHashMap<>(initialCapacity);
		});
	}

	@Parameters(name = "{0}, k={1}, {2}, isRequestedInitialCapacityPassedAlong={3}")
	public static Collection<Object[]> getConstructorArguments() {
		return createConstructorArgumentsForMappableImplementationsWithFalseAndTrue();
	}
}

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
package kmer;

import static edu.wustl.cse231s.v5.V5.launchAppWithReturn;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.IntFunction;

import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.Test;

import kmer.core.KMerCount;
import kmer.core.KMerCounter;
import kmer.core.map.MapKMerCount;
import kmer.lab.longconcurrentmap.LongConcurrentMapKMerCounter;
import kmer.util.KMerResource;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public abstract class AbstractConcurrentMapFactoryTest {
	protected abstract ConcurrentMap<Long, Integer> createMap(int initialCapacity);

	@Test
	public void test()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		MutableObject<ConcurrentMap<Long, Integer>> mapFactoryProduct = new MutableObject<>();
		IntFunction<ConcurrentMap<Long, Integer>> concurrentMapFactory = (initialCapacity) -> {
			assertNull("concurrentMapFactory should be invoked only once", mapFactoryProduct.getValue());
			ConcurrentMap<Long, Integer> result = createMap(initialCapacity);
			mapFactoryProduct.setValue(result);
			return result;
		};

		KMerCounter kMerCounter = new LongConcurrentMapKMerCounter(concurrentMapFactory);
		assertNull("concurrentMapFactory should not be invoked in the LongConcurrentMapKMerCounter constructor",
				mapFactoryProduct.getValue());
		List<byte[]> sequences = KMerResource.CHOLERAE_ORI_C.getSubSequences();
		KMerCount actualKMerCount = launchAppWithReturn(() -> {
			return kMerCounter.parse(sequences, 23);
		});

		assertNotNull("concurrentMapFactory should be invoked in parse method", mapFactoryProduct.getValue());

		assertNotNull(actualKMerCount);
		assertSame(MapKMerCount.class, actualKMerCount.getClass());
		@SuppressWarnings("unchecked")
		Map<Long, Integer> actualMap = accessMap((MapKMerCount<Long>) actualKMerCount);
		assertSame(
				"the map used to construct the KMerCount should be the same map which was returned from the concurrentMapFactory",
				mapFactoryProduct.getValue(), actualMap);
	}

	@SuppressWarnings("unchecked")
	private static Map<Long, Integer> accessMap(MapKMerCount<Long> mapKMerCount)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = MapKMerCount.class.getDeclaredField("map");
		field.setAccessible(true);
		return (Map<Long, Integer>) field.get(mapKMerCount);
	}
}

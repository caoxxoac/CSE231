/*******************************************************************************
 * Copyright (C) 2016-2017 Dennis Cosgrove, Ben Choi
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
package util.lab.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import util.lab.rubric.UtilRubric;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public abstract class AbstractPutTest<K> extends AbstractMapTest {
	public AbstractPutTest(CollectionSupplier collectionSupplier) {
		super(collectionSupplier);
	}

	protected abstract K createKey(double d);

	@Test
	public void test()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Map<K, Integer> map = this.createMap();

		double k = 0.01;
		K key = createKey(k);

		Collection<Entry<K, Integer>>[] buckets = AccessBucketHashMapUtils
				.getBucketsFieldValue((BucketsHashMap<K, Integer>) map);
		int expectedIndex = Math.floorMod(key.hashCode(), buckets.length);

		Collection<Entry<K, Integer>> bucket = buckets[expectedIndex];
		assertEquals(0, bucket.size());

		// test the first association of key
		Integer a = 1;
		Integer prevValueA = map.put(key, a);
		assertEquals(1, bucket.size());
		Entry<K, Integer> entryA = bucket.iterator().next();
		assertEquals(key, entryA.getKey());
		assertEquals(a, entryA.getValue());

		assertNotEquals(prevValueA, a);
		assertNull(prevValueA);

		// test assigning a new association of key
		Integer b = 2;
		Integer prevValueB = map.put(key, b);
		assertEquals(1, bucket.size());
		Entry<K, Integer> entryB = bucket.iterator().next();
		assertEquals(key, entryB.getKey());
		assertEquals(b, entryB.getValue());
		assertNotNull(prevValueB);
		assertNotEquals(prevValueB, b);
		assertEquals(a, prevValueB);

		// test assigning a new association of key with the wrinkle that it is a new
		// instance which equals the previous key but is not the same instance as the
		// previous key
		//
		// this forces the correct use of Objects.equals(a,b)
		// instead of the incorrect a==b
		//
		// if you get this far, passed the Primitive test but failed the NonPrimitive
		// test then you are likely incorrectly using ==
		// Switch to Objects.equals(a,b)
		K keyC = this.createKey(k);
		assertEquals(key, keyC);
		assertNotSame(key, keyC);

		Integer c = 3;
		Integer prevValueC = map.put(keyC, c);

		Entry<K, Integer> entryC = bucket.iterator().next();
		assertEquals(keyC, entryC.getKey());
		assertEquals(c, entryC.getValue());

		assertNotNull(prevValueC);
		assertNotEquals(prevValueC, c);
		assertNotEquals(prevValueC, a);
		assertEquals(b, prevValueC);
		assertEquals(1, bucket.size());
	}
}

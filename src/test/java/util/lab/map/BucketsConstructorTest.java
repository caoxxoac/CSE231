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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.wustl.cse231s.junit.JUnitUtils;
import util.lab.rubric.UtilRubric;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link BucketsHashMap#BucketsHashMap(java.util.function.Supplier)}
 */
@UtilRubric(UtilRubric.Category.MAP_CONSTRUCTOR)
@RunWith(Parameterized.class)
public class BucketsConstructorTest {
	private final int capacity;
	private final CollectionSupplier collectionSupplier;

	public BucketsConstructorTest(int capacity, CollectionSupplier collectionSupplier) {
		this.capacity = capacity;
		this.collectionSupplier = collectionSupplier;
	}

	@Test
	public void test()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		List<Collection<Entry<Object, Object>>> createdCollections = new ArrayList<>(capacity);
		BucketsHashMap<Object, Object> map = new BucketsHashMap<>(capacity, () -> {
			Collection<Entry<Object, Object>> collection = collectionSupplier.getSupplier().get();
			createdCollections.add(collection);
			return collection;
		});

		Collection<Entry<Object, Object>>[] buckets = AccessBucketHashMapUtils.getBucketsFieldValue(map);

		assertNotNull(buckets);
		assertEquals(capacity, buckets.length);
		for (int i = 0; i < buckets.length; i++) {
			assertNotNull(buckets[i]);
		}

		assertEquals(capacity, createdCollections.size());
		for (int i = 0; i < buckets.length; i++) {
			assertSame(createdCollections.get(i), buckets[i]);
		}
	}

	@Parameters(name = "capacity={0}; supplier={1}")
	public static Collection<Object[]> getConstructorArguments() {
		return JUnitUtils.toParameterizedArguments2(new Integer[] { 1, 2, 7, 71, 231, 1024 },
				CollectionSupplier.values());
	}
}

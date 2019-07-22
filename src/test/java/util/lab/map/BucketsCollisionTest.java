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
package util.lab.map;

import static org.junit.Assert.assertEquals;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import util.lab.rubric.UtilRubric;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link BucketsHashMap}
 */
public class BucketsCollisionTest extends AbstractMapTest {
	public BucketsCollisionTest(CollectionSupplier collectionSupplier) {
		super(collectionSupplier);
	}

	@UtilRubric(UtilRubric.Category.MAP_PUT)
	@Test
	public void testPut() {
		Map<ValidButPessimalKey, Integer> truthAndBeauty = new HashMap<>();
		Map<ValidButPessimalKey, Integer> studentMap = this.createMap();
		putOnBoth(truthAndBeauty, studentMap, new ValidButPessimalKey("fred"), 231);
		putOnBoth(truthAndBeauty, studentMap, new ValidButPessimalKey("george"), 341);
	}

	@UtilRubric(UtilRubric.Category.MAP_REMOVE)
	@Test
	public void testPutAndRemove() {
		Map<ValidButPessimalKey, Integer> truthAndBeauty = new HashMap<>();
		Map<ValidButPessimalKey, Integer> studentMap = this.createMap();

		@SuppressWarnings("unchecked")
		Entry<ValidButPessimalKey, Integer>[] entries = new Entry[] {
				new AbstractMap.SimpleEntry<>(new ValidButPessimalKey("A"), 1),
				new AbstractMap.SimpleEntry<>(new ValidButPessimalKey("B"), 2),
				new AbstractMap.SimpleEntry<>(new ValidButPessimalKey("C"), 3),
				new AbstractMap.SimpleEntry<>(new ValidButPessimalKey("D"), 4),
				new AbstractMap.SimpleEntry<>(new ValidButPessimalKey("E"), 5), };

		for (int i = 0; i < 2; i++) {
			for (Entry<ValidButPessimalKey, Integer> entry : entries) {
				putOnBoth(truthAndBeauty, studentMap, entry.getKey(), entry.getValue());
			}

			if (i == 1) {
				ArrayUtils.reverse(entries);
			}
			for (Entry<ValidButPessimalKey, Integer> entry : entries) {
				removeFromBoth(truthAndBeauty, studentMap, entry.getKey());
			}
		}
	}

	private static <K, V> void assertMapsAreEquivalent(Map<K, V> truthAndBeauty, Map<K, V> studentMap) {
		assertEquals(truthAndBeauty.isEmpty(), studentMap.isEmpty());
		assertEquals(truthAndBeauty.size(), studentMap.size());
		for (Entry<K, V> entry : truthAndBeauty.entrySet()) {
			assertEquals(entry.getValue(), studentMap.get(entry.getKey())); // TODO: check ==
		}
	}

	private static <K, V> void putOnBoth(Map<K, V> truthAndBeauty, Map<K, V> studentMap, K key, V value) {
		truthAndBeauty.put(key, value);
		studentMap.put(key, value);
		assertMapsAreEquivalent(truthAndBeauty, studentMap);
	}

	private static <K, V> void removeFromBoth(Map<K, V> truthAndBeauty, Map<K, V> studentMap, K key) {
		V expectedValue = truthAndBeauty.remove(key);
		V actualValue = studentMap.remove(key);
		assertEquals(expectedValue, actualValue);
		assertMapsAreEquivalent(truthAndBeauty, studentMap);
	}
}

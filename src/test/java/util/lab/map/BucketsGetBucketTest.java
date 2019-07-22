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

import static org.junit.Assert.assertSame;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
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
@UtilRubric(UtilRubric.Category.MAP_GET_BUCKET_FOR)
@RunWith(Parameterized.class)
public class BucketsGetBucketTest {
	private final int capacity;
	private final int value;

	public BucketsGetBucketTest(int capacity, int value) {
		this.capacity = capacity;
		this.value = value;
	}

	@Test
	public void test() throws NoSuchFieldException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		BucketsHashMap<Integer, Void> map = new BucketsHashMap<>(capacity, CollectionSupplier.JAVA_UTIL.getSupplier());
		Collection<Entry<Integer, Void>>[] buckets = AccessBucketHashMapUtils.getBucketsFieldValue(map);
		int actualIndex = AccessBucketHashMapUtils.getBucketIndex(map, value);
		Collection<Entry<Integer, Void>> bucket = AccessBucketHashMapUtils.getBucketFor(map, value);
		// assertThat(actualIndex, greaterThanOrEqualTo(0));
		// assertThat(actualIndex, lessThan(capacity));
		assertSame(buckets[actualIndex], bucket);
	}

	@Parameters(name = "capacity={0}; value={1}")
	public static Collection<Object[]> getConstructorArguments() {
		return JUnitUtils.toParameterizedArguments2(new Integer[] { 71, 231 },
				new Integer[] { 0, 1, 2, 3, 10, 100, 1000, -1, -1000, Integer.MIN_VALUE });
	}
}

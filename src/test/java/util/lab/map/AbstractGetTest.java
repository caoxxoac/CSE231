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
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;

import util.lab.rubric.UtilRubric;

/**
 * @author Ben Choi (benjaminchoi@wustl.edu)
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link BucketsHashMap#get(Object)}
 */
@UtilRubric(UtilRubric.Category.MAP_GET)
public abstract class AbstractGetTest<K> extends AbstractMapTest {
	public AbstractGetTest(CollectionSupplier collectionSupplier) {
		super(collectionSupplier);
	}

	protected abstract K createKey(int k);

	@Test
	public void test() {
		Map<K, Double> map = this.createMap();

		for (int i = 0; i < 10; ++i) {
			K key = createKey(i);
			Double value = i * 0.1;
			assertNull("Put should return the previous value", map.put(key, value));
			assertNotNull("Your map is not returning a value when it should", map.get(key));
			assertEquals("Your map is not returning the correct value", value, map.get(key));

			K notAssociatedKey = createKey(i + 1);
			assertNull("Your map is returning a value when it shouldn't be", map.get(notAssociatedKey));
		}
	}
}

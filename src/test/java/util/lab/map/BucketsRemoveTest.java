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
 *         {@link BucketsHashMap#remove(Object)}
 */
@UtilRubric(UtilRubric.Category.MAP_REMOVE)
public class BucketsRemoveTest extends AbstractMapTest {
	public BucketsRemoveTest(CollectionSupplier collectionSupplier) {
		super(collectionSupplier);
	}

	@Test
	public void test() {
		Map<Integer, Double> map = this.createMap();

		for (int i = 0; i < 10; ++i) {
			Integer key = i;
			Double value = toValue(i);
			assertNull("Something is wrong with your put method, fix that first", map.put(key, value));
		}

		for (int i = 0; i < 10; ++i) {
			Double expectedValue = toValue(i);
			Double prevValue = map.remove(i);
			assertNotNull("The remove should return the old value after deletion", prevValue);
			assertEquals("The remove should return the old value after deletion", expectedValue, prevValue);
		}

		assertEquals("Your size function is not updating properly", 0, map.size());
	}

	private static double toValue(int i) {
		return i * 0.1;
	}
}

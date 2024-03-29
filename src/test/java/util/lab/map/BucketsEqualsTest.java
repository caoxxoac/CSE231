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
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;

import util.lab.rubric.UtilRubric;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link BucketsHashMap}
 */
public class BucketsEqualsTest extends AbstractMapTest {
	public BucketsEqualsTest(CollectionSupplier collectionSupplier) {
		super(collectionSupplier);
	}

	@UtilRubric(UtilRubric.Category.MAP_GET)
	@Test
	public void testPrimitiveGet() {
		Map<Integer, String> map = this.createMap();
		assertEquals(0, map.size());
		map.put(1, "one");
		assertEquals(1, map.size());
		assertEquals("one", map.get(1));
	}

	@UtilRubric(UtilRubric.Category.MAP_GET)
	@Test
	public void testNonPrimitiveGet() {
		Map<BigDecimal, String> map = this.createMap();
		assertEquals(0, map.size());
		map.put(new BigDecimal(1), "one");
		assertEquals(1, map.size());
		assertEquals("one", map.get(new BigDecimal(1)));
	}

	@UtilRubric(UtilRubric.Category.MAP_PUT)
	@Test
	public void testPrimitivePut() {
		Map<Integer, String> map = this.createMap();
		assertEquals(0, map.size());
		map.put(1, "one");
		assertEquals(1, map.size());
		map.put(1, "uno");
		assertEquals(1, map.size());
		assertEquals("uno", map.get(1));
	}

	@UtilRubric(UtilRubric.Category.MAP_PUT)
	@Test
	public void testNonPrimitivePut() {
		Map<BigDecimal, String> map = this.createMap();
		assertEquals(0, map.size());
		map.put(new BigDecimal(1), "one");
		assertEquals(1, map.size());
		map.put(new BigDecimal(1), "uno");
		assertEquals(1, map.size());
		assertEquals("uno", map.get(new BigDecimal(1)));
	}

	@UtilRubric(UtilRubric.Category.MAP_REMOVE)
	@Test
	public void testPrimitiveRemove() {
		Map<Integer, String> map = this.createMap();
		assertEquals(0, map.size());
		map.put(1, "one");
		assertEquals(1, map.size());
		map.remove(1);
		assertEquals(0, map.size());
		assertNull(map.get(1));
	}

	@UtilRubric(UtilRubric.Category.MAP_REMOVE)
	@Test
	public void testNonPrimitiveRemove() {
		Map<BigDecimal, String> map = this.createMap();
		assertEquals(0, map.size());
		map.put(new BigDecimal(1), "one");
		assertEquals(1, map.size());
		map.remove(new BigDecimal(1));
		assertEquals(0, map.size());
		assertNull(map.get(new BigDecimal(1)));
	}
}

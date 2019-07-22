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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;

import util.lab.rubric.UtilRubric;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link BucketsHashMap}
 */
@UtilRubric(UtilRubric.Category.MAP_PUT)
public class BucketsWordCountComputeTest extends AbstractMapTest {
	public BucketsWordCountComputeTest(CollectionSupplier collectionSupplier) {
		super(collectionSupplier);
	}

	@Test
	public void testNotPresent() {
		Map<String, Integer> map = this.createMap();

		map.compute("Romeo", (unusedKey, value) -> {
			assertNull(value);
			return 1;
		});
	}

	@Test
	public void testPresent() {
		Map<String, Integer> map = this.createMap();

		map.compute("Romeo", (key, value) -> {
			return 1;
		});

		map.compute("Romeo", (key, value) -> {
			assertNotNull(value);
			assertEquals(1, value.intValue());
			return value + 1;
		});
	}

	@Test
	public void testWordCount() {
		Map<String, Integer> map = this.createMap();

		// source: Romeo and Juliet, Act II, Scene 2, Shakespeare
		String line = "O Romeo, Romeo! wherefore art thou Romeo?";

		String[] words = line.split("\\W+");

		for (String word : words) {
			map.compute(word, (key, value) -> {
				assertEquals(word, key);
				if (value != null) {
					return value + 1;
				} else {
					return 1;
				}
			});
		}

		assertFalse(map.isEmpty());
		assertEquals(5, map.size());
		assertEquals(1, map.get("O").intValue());
		assertEquals(3, map.get("Romeo").intValue());
		assertEquals(1, map.get("wherefore").intValue());
		assertEquals(1, map.get("art").intValue());
		assertEquals(1, map.get("thou").intValue());
		assertNull(map.get("Forget"));
	}
}

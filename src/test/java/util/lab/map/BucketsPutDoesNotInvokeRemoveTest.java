/*******************************************************************************
 * Copyright (C) 2016-2018 Dennis Cosgrove
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

import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

import util.lab.collection.LinkedCollection;
import util.lab.rubric.UtilRubric;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link BucketsHashMap#put(Object)}
 */
@UtilRubric(UtilRubric.Category.MAP_PUT)
public class BucketsPutDoesNotInvokeRemoveTest {
	@Test
	public void test() {
		class ConstrainedCollection<E> extends LinkedCollection<E> {
			@Override
			public Iterator<E> iterator() {
				Iterator<E> s = super.iterator();
				return new Iterator<E>() {
					@Override
					public boolean hasNext() {
						return s.hasNext();
					}

					@Override
					public E next() {
						return s.next();
					}

					@Override
					public void remove() {
						assertTrue(
								"put(key, value) should not invoke remove, rather it should simply change the value of the entry if it finds the specified key.",
								false);
					}
				};
			}
		}
		Map<String, String> map = new BucketsHashMap<>(() -> {
			return new ConstrainedCollection<>();
		});

		map.put("Paddington", "Peru");
		map.put("Paddington", "London");
	}

}

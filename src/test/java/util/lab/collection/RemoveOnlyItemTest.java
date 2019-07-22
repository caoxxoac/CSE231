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
package util.lab.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import edu.wustl.cse231s.junit.JUnitUtils;
import util.lab.rubric.UtilRubric;

/**
 * @author Dennis Cosgrove (cosgroved@wustl.edu)
 *         {@link LinkedCollection#iterator()} 
 *         {@link LinkedIterator#remove()}
 */
@UtilRubric(UtilRubric.Category.ITERATOR_REMOVE)
public class RemoveOnlyItemTest {
	@Rule
	public TestRule timeout = JUnitUtils.createTimeoutRule();

	@Test
	public void test() {
		String itemA = "a";
		LinkedCollection<String> list = new LinkedCollection<String>();
		assertTrue(list.add(itemA));
		assertEquals(1, list.size());
		assertTrue(list.iterator().hasNext());
		assertEquals(itemA, list.iterator().next());
		assertTrue(list.remove(itemA));
		assertEquals(0, list.size());
		assertFalse(list.iterator().hasNext());

		// ensure the collection is still in good shape after remove
		String itemB = "b";
		assertTrue(list.add(itemB));
		assertEquals(1, list.size());
		assertTrue(list.iterator().hasNext());
		assertEquals(itemB, list.iterator().next());
	}

}

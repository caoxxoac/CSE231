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

package util.lab.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import edu.wustl.cse231s.junit.JUnitUtils;
import util.lab.rubric.UtilRubric;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link LinkedNodesIterator#remove()}
 */
@UtilRubric(UtilRubric.Category.ITERATOR_REMOVE)
public class IteratorRemoveIllegalStateExceptionTest {
	@Rule
	public TestRule timeout = JUnitUtils.createTimeoutRule();

	@Test(expected = IllegalStateException.class)
	public void testRemoveForEmpty() {
		Collection<Void> collection = new LinkedCollection<>();
		Iterator<Void> iterator = collection.iterator();
		assertFalse(iterator.hasNext());
		iterator.remove(); // this line should throw an IllegalStateException
	}

	@Test(expected = IllegalStateException.class)
	public void testRemoveAtBeginning() {
		int value = 71;
		Collection<Integer> collection = new LinkedCollection<>();
		collection.add(value);
		Iterator<Integer> iterator = collection.iterator();
		assertTrue(iterator.hasNext());
		iterator.remove(); // this line should throw an IllegalStateException
	}

	@Test(expected = IllegalStateException.class)
	public void testRemoveTwice1() {
		Collection<Integer> collection = new LinkedCollection<>();
		collection.add(1);
		collection.add(2);
		collection.add(3);
		Iterator<Integer> iterator = collection.iterator();
		assertTrue(iterator.hasNext());
		int value = iterator.next();
		assertEquals(1, value);
		iterator.remove();
		iterator.remove(); // this line should throw an IllegalStateException
	}

	@Test(expected = IllegalStateException.class)
	public void testRemoveTwice2() {
		Collection<Integer> collection = new LinkedCollection<>();
		collection.add(1);
		collection.add(2);
		collection.add(3);
		Iterator<Integer> iterator = collection.iterator();

		assertTrue(iterator.hasNext());
		int value1 = iterator.next();
		assertEquals(1, value1);

		assertTrue(iterator.hasNext());
		int value2 = iterator.next();
		assertEquals(2, value2);

		iterator.remove();
		iterator.remove(); // this line should throw an IllegalStateException
	}

	@Test(expected = IllegalStateException.class)
	public void testRemoveTwice3() {
		Collection<Integer> collection = new LinkedCollection<>();
		collection.add(1);
		collection.add(2);
		collection.add(3);
		Iterator<Integer> iterator = collection.iterator();

		assertTrue(iterator.hasNext());
		int value1 = iterator.next();
		assertEquals(1, value1);

		assertTrue(iterator.hasNext());
		int value2 = iterator.next();
		assertEquals(2, value2);

		assertTrue(iterator.hasNext());
		int value3 = iterator.next();
		assertEquals(3, value3);

		iterator.remove();
		iterator.remove(); // this line should throw an IllegalStateException
	}
}

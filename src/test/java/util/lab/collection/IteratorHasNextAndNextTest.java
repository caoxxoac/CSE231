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

import java.util.Iterator;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runners.MethodSorters;

import edu.wustl.cse231s.junit.JUnitUtils;
import util.lab.rubric.UtilRubric;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link LinkedIterator#hasNext()} {@link LinkedIterator#next()}
 */
@UtilRubric(UtilRubric.Category.ITERATOR_HAS_NEXT_AND_NEXT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IteratorHasNextAndNextTest {
	@Test
	public void testA() {
		Integer value = 71;

		DoublyLinkedNode<Integer> sentinel = DoublyLinkedNodeUtils.createSentinel();
		DoublyLinkedNode<Integer> node = new DoublyLinkedNode<Integer>(value);
		DoublyLinkedNodeUtils.hookUpNodes(sentinel, node);

		Iterator<Integer> iterator = DoublyLinkedNodeUtils.createIterator(sentinel);
		assertTrue(iterator.hasNext());
		assertEquals(value, iterator.next());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testAB() {
		Integer a = 71;
		Integer b = 231;

		DoublyLinkedNode<Integer> sentinel = DoublyLinkedNodeUtils.createSentinel();
		DoublyLinkedNode<Integer> aNode = new DoublyLinkedNode<Integer>(a);
		DoublyLinkedNode<Integer> bNode = new DoublyLinkedNode<Integer>(b);
		DoublyLinkedNodeUtils.hookUpNodes(sentinel, aNode);
		DoublyLinkedNodeUtils.hookUpNodes(aNode, bNode);

		Iterator<Integer> iterator = DoublyLinkedNodeUtils.createIterator(sentinel);
		assertTrue(iterator.hasNext());
		assertEquals(a, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(b, iterator.next());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testABC() {
		Integer a = 71;
		Integer b = 231;
		Integer c = 425;

		DoublyLinkedNode<Integer> sentinel = DoublyLinkedNodeUtils.createSentinel();
		DoublyLinkedNode<Integer> aNode = new DoublyLinkedNode<Integer>(a);
		DoublyLinkedNode<Integer> bNode = new DoublyLinkedNode<Integer>(b);
		DoublyLinkedNode<Integer> cNode = new DoublyLinkedNode<Integer>(c);
		DoublyLinkedNodeUtils.hookUpNodes(sentinel, aNode);
		DoublyLinkedNodeUtils.hookUpNodes(aNode, bNode);
		DoublyLinkedNodeUtils.hookUpNodes(bNode, cNode);

		Iterator<Integer> iterator = DoublyLinkedNodeUtils.createIterator(sentinel);
		assertTrue(iterator.hasNext());
		assertEquals(a, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(b, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(c, iterator.next());
		assertFalse(iterator.hasNext());
	}

	@Rule
	public TestRule timeout = JUnitUtils.createTimeoutRule();
}

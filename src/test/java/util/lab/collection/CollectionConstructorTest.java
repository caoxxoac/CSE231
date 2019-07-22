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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import util.lab.rubric.UtilRubric;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
@UtilRubric(UtilRubric.Category.COLLECTION_CONSTRUCTOR)
public class CollectionConstructorTest {
	@Test
	public void test()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		LinkedCollection<Integer> collection = new LinkedCollection<>();

		DoublyLinkedNode<Integer> head = AccessCollectionFieldUtils.getHeadFieldValue(collection);
		DoublyLinkedNode<Integer> tail = AccessCollectionFieldUtils.getTailFieldValue(collection);
		int size = AccessCollectionFieldUtils.getSizeFieldValue(collection);

		assertNotNull(head);
		assertNotNull(tail);

		assertSame(head, tail);

		assertNull(head.getValue());
		assertNull(head.getPrev());
		assertNull(head.getNext());

		assertEquals(0, size);
	}
}

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
package util.lab.collection;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.wustl.cse231s.junit.JUnitUtils;
import util.lab.rubric.UtilRubric;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
@UtilRubric(UtilRubric.Category.ITERATOR_REMOVE)
@RunWith(Parameterized.class)
public class IteratorRemoveTailUpdaterTest {
	private final NodesCreator nodesCreator;
	private final List<Integer> values;
	private final List<Integer> valuesToRemove;

	public IteratorRemoveTailUpdaterTest(NodesCreator nodesCreator, List<Integer> values,
			List<Integer> valuesToRemove) {
		this.nodesCreator = nodesCreator;
		this.values = values;
		this.valuesToRemove = valuesToRemove;
	}

	@Test
	public void test() throws Exception {
		MutableInt tailUpdaterInvocationCount = new MutableInt();
		MutableObject<DoublyLinkedNode<Integer>> updatedTail = new MutableObject<>();
		DoublyLinkedNode<Integer> head = nodesCreator.createNodes(values);
		Iterator<Integer> iterator = new LinkedIterator<>(head, (tail) -> {
			tailUpdaterInvocationCount.increment();
			updatedTail.setValue(tail);
		}, () -> {
		});

		int lastValue = values.get(values.size() - 1);

		DoublyLinkedNode<Integer> tailToBe = head;
		DoublyLinkedNode<Integer> curr = head.getNext();
		while (curr != null) {
			if (valuesToRemove.contains(curr.getValue())) {
				// pass
			} else {
				tailToBe = curr;
			}
			curr = curr.getNext();
		}

		DoublyLinkedNode<Integer> expectedTail = null;
		int expectedInvocationCount = 0;
		assertEquals(expectedInvocationCount, tailUpdaterInvocationCount.intValue());
		while (iterator.hasNext()) {
			int value = iterator.next();
			assertEquals(expectedInvocationCount, tailUpdaterInvocationCount.intValue());
			assertEquals(expectedTail, updatedTail.getValue());
			if (valuesToRemove.contains(value)) {
				iterator.remove();
				if (value == lastValue) {
					expectedInvocationCount++;
					expectedTail = tailToBe;
				}
				assertEquals(expectedInvocationCount, tailUpdaterInvocationCount.intValue());
				assertEquals(expectedTail, updatedTail.getValue());
			}
		}
	}

	@Parameters(name = "{0}; values={1}; valuesToRemove={2}")
	public static Collection<Object[]> getConstructorArguments() {
		NodesCreator nodesCreator = NodesCreator.DIRECT_CREATION;
		List<Integer> values = Arrays.asList(1, 2, 3, 4, 5);
		List<Object[]> result = new LinkedList<>();
		result.add(new Object[] { nodesCreator, values, Collections.emptyList() });
		for (int value : values) {
			result.add(new Object[] { nodesCreator, values, Arrays.asList(value) });
		}
		result.add(new Object[] { nodesCreator, values, values });
		return result;
	}

	@Rule
	public TestRule timeout = JUnitUtils.createTimeoutRule();
}

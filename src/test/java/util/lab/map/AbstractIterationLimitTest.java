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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.Test;

import util.lab.collection.LinkedCollection;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link BucketsHashMap#get(Object)}
 */
public abstract class AbstractIterationLimitTest {
	private final boolean isTestingGetDesired;

	public AbstractIterationLimitTest(boolean isTestingGetDesired) {
		this.isTestingGetDesired = isTestingGetDesired;
	}

	@Test
	public void test() {
		MutableObject<Collection<Entry<Integer, String>>> iteratedCollection = new MutableObject<>();
		MutableInt iteratorCount = new MutableInt();
		class ConstrainedCollection<E> extends LinkedCollection<E> {
			@Override
			public Iterator<E> iterator() {
				Iterator<E> result = super.iterator();
				iteratorCount.increment();
				Collection<Entry<Integer, String>> prevIteratedCollection = iteratedCollection.getValue();
				if (prevIteratedCollection != null) {
					assertSame("only a single bucket should be iterated over", this, prevIteratedCollection);
				} else {
					iteratedCollection.setValue((Collection) this);
				}
				return result;
			}
		}
		Map<Integer, String> map = new BucketsHashMap<>(1024, () -> {
			return new ConstrainedCollection<>();
		});

		Integer key = 71;
		String value = "Score";
		assertEquals(0, iteratorCount.intValue());
		map.put(key, value);
		assertNotEquals("put(key,value) should be invoking iterator() via a for-each loop", 0,
				iteratorCount.intValue());
		assertEquals("put(key, value) should only need to invoke iterator() via a for-each loop once", 1,
				iteratorCount.intValue());

		if (isTestingGetDesired) {
			iteratorCount.setValue(0);
			map.get(key);
			assertNotEquals("get(key) should be invoking iterator() via a for-each loop", 0, iteratorCount.intValue());
			assertEquals("get(key) should only need to invoke iterator() via a for-each loop once", 1,
					iteratorCount.intValue());
		}
	}

}

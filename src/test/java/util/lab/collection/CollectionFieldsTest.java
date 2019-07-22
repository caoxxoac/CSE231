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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.Test;

import util.lab.rubric.UtilRubric;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
@UtilRubric(UtilRubric.Category.COLLECTION_CONSTRUCTOR)
public class CollectionFieldsTest {
	@Test
	public void test() throws NoSuchFieldException, SecurityException {
		Field headField = CollectionFieldUtils.getHeadField();
		Field tailField = CollectionFieldUtils.getTailField();
		Field sizeField = CollectionFieldUtils.getSizeField();

		int expectedHeadModifiers = Modifier.PRIVATE | Modifier.FINAL;
		int actualHeadModifiers = headField.getModifiers();
		assertEquals(toModifiersMessage(headField, expectedHeadModifiers, actualHeadModifiers), expectedHeadModifiers,
				actualHeadModifiers);

		int expectedTailModifiers = Modifier.PRIVATE;
		int actualTailModifiers = tailField.getModifiers();
		assertEquals(toModifiersMessage(headField, expectedTailModifiers, actualHeadModifiers), expectedTailModifiers,
				actualTailModifiers);

		int expectedSizeModifiers = Modifier.PRIVATE;
		int actualSizeModifiers = sizeField.getModifiers();
		assertEquals(toModifiersMessage(headField, expectedHeadModifiers, actualHeadModifiers), expectedSizeModifiers,
				actualSizeModifiers);
	}

	private static String toModifiersMessage(Field field, int expected, int actual) {
		return "\nmodifiers for " + field.getName() + ": expected: <" + Modifier.toString(expected) + ">; actual: <"
				+ Modifier.toString(actual) + ">\n";
	}
}

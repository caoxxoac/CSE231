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

package edu.wustl.cse231s.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class ReflectionUtils {
	public static <T, R> R getDeclaredInaccessibleFieldValue(Field field, T instance)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		field.setAccessible(true);
		@SuppressWarnings("unchecked")
		R value = (R) field.get(instance);
		return value;
	}

	public static <T, R> R getDeclaredInaccessibleFieldValue(Class<T> cls, String fieldName, T instance)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		return getDeclaredInaccessibleFieldValue(cls.getDeclaredField(fieldName), instance);
	}

	public static <T, R> R getDeclaredInaccessibleStaticFieldValue(Class<T> cls, String fieldName)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		return getDeclaredInaccessibleFieldValue(cls, fieldName, null);
	}

	public static <T, R> R invokeDeclaredInaccessibleMethod(Class<T> cls, String methodName, Class<?>[] parameterTypes,
			T instance, Object... args) throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Method method = cls.getDeclaredMethod(methodName, parameterTypes);
		method.setAccessible(true);
		@SuppressWarnings("unchecked")
		R returnValue = (R) method.invoke(instance, args);
		return returnValue;
	}

	public static <T, R> R invokeDeclaredInaccessibleStaticMethod(Class<T> cls, String methodName,
			Class<?>[] parameterTypes, Object... args) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return invokeDeclaredInaccessibleMethod(cls, methodName, parameterTypes, null, args);
	}

	public static Field getOneAndOnlyOneDeclaredFieldContainingIgnoringCase(Class<?> cls,
			String... lowerCaseCandidateFieldNames) {

		Field[] fields = cls.getDeclaredFields();
		List<Field> foundFields = new LinkedList<>();
		for (Field field : fields) {
			for (String lowerCaseCandidateFieldName : lowerCaseCandidateFieldNames) {
				if (field.getName().toLowerCase().contains(lowerCaseCandidateFieldName)) {
					foundFields.add(field);
					break;
				}
			}
		}
		if (foundFields.size() == 1) {
			return foundFields.get(0);
		} else {
			String candidateText = lowerCaseCandidateFieldNames.length == 1 ? lowerCaseCandidateFieldNames[0]
					: "one of " + Arrays.toString(lowerCaseCandidateFieldNames);
			if (foundFields.size() > 1) {
				StringBuilder allFieldsSB = new StringBuilder();
				for (Field field : fields) {
					allFieldsSB.append(foundFields.contains(field) ? "*** " : "    ");
					allFieldsSB.append(field.getName());
					allFieldsSB.append("\n");
				}
				throw new RuntimeException("more than one of the declared instance variables:\n"
						+ allFieldsSB.toString() + "contains (ignoring case) " + candidateText
						+ ".\nIn order to provide early test results, we regretably require that only one instance variable contains "
						+ candidateText);
			} else {
				StringBuilder allFieldsSB = new StringBuilder();
				for (Field field : fields) {
					allFieldsSB.append("  ");
					allFieldsSB.append(field.getName());
					allFieldsSB.append("\n");
				}
				throw new RuntimeException("out of all declared instance variables:\n" + allFieldsSB.toString()
						+ "none contain (even ignoring case) " + candidateText);
			}
		}
	}

}

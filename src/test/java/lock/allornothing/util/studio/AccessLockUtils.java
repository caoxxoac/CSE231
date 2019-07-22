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
package lock.allornothing.util.studio;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */

import java.util.List;
import java.util.concurrent.locks.Lock;

import edu.wustl.cse231s.reflection.ReflectionUtils;
import lock.allornothing.util.studio.LockUtils;

class AccessLockUtils {
	static boolean tryLockAll(List<? extends Lock> locks) {
		try {
			return ReflectionUtils.invokeDeclaredInaccessibleStaticMethod(LockUtils.class, "tryLockAll",
					new Class[] { List.class }, locks);
		} catch (InvocationTargetException ite) {
			Throwable cause = ite.getCause();
			if (cause instanceof RuntimeException) {
				RuntimeException re = (RuntimeException) cause;
				throw re;
			} else if (cause instanceof Error) {
				Error error = (Error) cause;
				throw error;
			} else {
				throw new RuntimeException(ite);
			}
		} catch (NoSuchMethodException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	static void unlockAll(List<? extends Lock> locks) {
		try {
			ReflectionUtils.invokeDeclaredInaccessibleStaticMethod(LockUtils.class, "unlockAll",
					new Class[] { List.class }, locks);
		} catch (InvocationTargetException ite) {
			Throwable cause = ite.getCause();
			if (cause instanceof RuntimeException) {
				RuntimeException re = (RuntimeException) cause;
				throw re;
			} else if (cause instanceof Error) {
				Error error = (Error) cause;
				throw error;
			} else {
				throw new RuntimeException(ite);
			}
		} catch (NoSuchMethodException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}

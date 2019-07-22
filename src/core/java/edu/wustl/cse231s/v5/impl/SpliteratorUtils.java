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
 *******************************************************************************/
package edu.wustl.cse231s.v5.impl;

import java.util.Spliterator;
import java.util.concurrent.ExecutionException;

import edu.wustl.cse231s.v5.api.CheckedConsumer;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class SpliteratorUtils {
	private static class ChainedException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		ChainedException(InterruptedException ie) {
			super(ie);
		}

		ChainedException(ExecutionException ee) {
			super(ee);
		}
	}

	public static <T> void forEachRemaining(Spliterator<T> spliterator, CheckedConsumer<T> body)
			throws InterruptedException, ExecutionException {
		try {
			spliterator.forEachRemaining((v) -> {
				try {
					body.accept(v);
				} catch (InterruptedException ie) {
					throw new ChainedException(ie);
				} catch (ExecutionException ee) {
					throw new ChainedException(ee);
				}
			});
		} catch (ChainedException ce) {
			Throwable cause = ce.getCause();
			if (cause instanceof InterruptedException) {
				InterruptedException ie = (InterruptedException) cause;
				throw ie;
			} else {
				ExecutionException ee = (ExecutionException) cause;
				throw ee;
			}
		}
	}
}

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

package edu.wustl.cse231s.v5.bookkeep;

import static edu.wustl.cse231s.v5.V5.launchApp;
import static org.junit.Assert.assertNotNull;

import java.util.function.Consumer;

import edu.wustl.cse231s.v5.api.Bookkeeping;
import edu.wustl.cse231s.v5.api.CheckedRunnable;
import edu.wustl.cse231s.v5.options.SystemPropertiesOption;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class BookkeepingUtils {
	public static void bookkeep(SystemPropertiesOption.Builder builder, CheckedRunnable body,
			Consumer<Bookkeeping> bookkeepingConsumer) {
		launchApp(builder.isBookkeepingDesired(true).build(), body, (metrics, bookkeeping) -> {
			assertNotNull(bookkeeping);
			bookkeepingConsumer.accept(bookkeeping);
		});
	}

	public static void bookkeep(CheckedRunnable body, Consumer<Bookkeeping> bookkeepingConsumer) {
		bookkeep(new SystemPropertiesOption.Builder(), body, bookkeepingConsumer);
	}
}

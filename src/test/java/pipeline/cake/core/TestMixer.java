/*******************************************************************************
 * Copyright (C) 2016-2019 Dennis Cosgrove
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
package pipeline.cake.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class TestMixer implements Mixer {
	private final MixedIngredients[] mixes;
	private final AtomicInteger count = new AtomicInteger(0);
	private Thread thread;

	public TestMixer(MixedIngredients[] mixes) {
		this.mixes = mixes;
	}

	@Override
	public MixedIngredients mix(int cakeIndex) {
		// Make sure the current cake being mixed is equal to the count
		assertEquals(count.get(), cakeIndex);
		if (count.get() == 0) {
			thread = Thread.currentThread();
		} else {
			// This assertion makes sure that all the mixing of ingredients occur within a
			// single async
			assertSame("Make sure all ingredients are mixed in the same thread.", thread, Thread.currentThread());
		}
		MixedIngredients mixedIngredients = new MixedIngredients(cakeIndex);
		mixes[count.get()] = mixedIngredients;
		count.incrementAndGet();
		return mixedIngredients;
	}

	public int getCount() {
		return count.intValue();
	}
}

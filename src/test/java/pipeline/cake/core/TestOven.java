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
public class TestOven implements Oven {
	private final BakedCake[] bakedCakes;
	private final int ovenCount;
	private final AtomicInteger count = new AtomicInteger(0);
	private Thread thread;

	public TestOven(BakedCake[] bakedCakes, int ovenCount) {
		this.bakedCakes = bakedCakes;
		this.ovenCount = ovenCount;
	}

	@Override
	public BakedCake bake(int cakeIndex, MixedIngredients mixedIngredients) {
		if (ovenCount == 1) {
			assertEquals(count.get(), cakeIndex);
		}
		if (count.get() == 0) {
			thread = Thread.currentThread();
		} else {
			assertSame("Make sure all cakes are baked in the same thread.", thread, Thread.currentThread());
		}
		BakedCake bakedCake = new BakedCake(cakeIndex, mixedIngredients);
		bakedCakes[count.get()] = bakedCake;
		count.incrementAndGet();
		return bakedCake;
	}

	public int getCount() {
		return count.intValue();
	}
}

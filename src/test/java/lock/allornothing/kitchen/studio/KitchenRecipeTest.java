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

package lock.allornothing.kitchen.studio;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

import lock.allornothing.kitchen.core.Appliance;
import lock.allornothing.kitchen.core.Recipe;

/**
 * @author Miles Cushing (mcushing@wustl.edu)
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link KitchenLockUtilsApp#executeRecipe(Recipe)}
 */
public class KitchenRecipeTest {
	private static class TestIdAppliance implements Appliance {
		private final int id;
		private final ReentrantLock lock = new ReentrantLock();

		public TestIdAppliance(int id) {
			this.id = id;
		}

		@Override
		public ReentrantLock getLock() {
			return lock;
		}

		@Override
		public String toString() {
			return "Appliance[" + id + "]";
		}
	}

	@Test
	public void test() throws InterruptedException {
		// Test 50 trials
		for (int i = 0; i < 50; i++) {
			// Make a recipe with a random number of locks needed between 1 and 30
			int numAppliances = 1 + (int) (Math.random() * 29);
			Appliance[] appliances = new Appliance[numAppliances];
			for (int j = 0; j < numAppliances; j++) {
				appliances[j] = new TestIdAppliance(j);
			}
			Recipe recipe = new Recipe(Arrays.asList(appliances));
			assertTrue(KitchenLockUtilsApp.executeRecipe(recipe));
		}
	}
}

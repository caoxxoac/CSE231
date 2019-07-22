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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import lock.allornothing.kitchen.core.Appliance;
import lock.allornothing.kitchen.core.Recipe;
import lock.allornothing.studio.ThreadTestUtils;

/**
 * @author Miles Cushing (mcushing@wustl.edu)
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link KitchenLockUtilsApp#executeRecipe(Recipe)}
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class KitchenRecipePreliminaryTest {
	private static enum TestAppliance implements Appliance {
		OVEN, STOVE, AIR_FRYER;
		private final ReentrantLock lock = new ReentrantLock();

		@Override
		public ReentrantLock getLock() {
			return lock;
		}
	}

	@Test
	public void testA_singleUncontestedRecipe() throws InterruptedException {
		Recipe dinner = new Recipe(Arrays.asList(TestAppliance.OVEN, TestAppliance.STOVE, TestAppliance.AIR_FRYER));
		assertTrue(KitchenLockUtilsApp.executeRecipe(dinner));
	}

	@Test
	public void testB_singleContestedRecipe() throws InterruptedException, ExecutionException {
		Recipe dinner = new Recipe(Arrays.asList(TestAppliance.OVEN, TestAppliance.STOVE, TestAppliance.AIR_FRYER));
		TestAppliance.OVEN.getLock().lock();
		Mutable<Boolean> isSuccessful = new MutableObject<>(null);
		ThreadTestUtils.startAndJoinThread(() -> {
			isSuccessful.setValue(KitchenLockUtilsApp.executeRecipe(dinner));
		});
		assertFalse(isSuccessful.getValue().booleanValue());
		TestAppliance.OVEN.getLock().unlock();
	}
}

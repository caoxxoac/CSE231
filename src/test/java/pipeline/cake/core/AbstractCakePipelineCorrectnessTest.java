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

import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import edu.wustl.cse231s.junit.JUnitUtils;
import edu.wustl.cse231s.v5.bookkeep.BookkeepingUtils;
import pipeline.cake.challenge.MultiOvenCakePipeline;
import pipeline.cake.studio.CakePipeline;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public abstract class AbstractCakePipelineCorrectnessTest {
	private final int ovenCount;

	public AbstractCakePipelineCorrectnessTest(int ovenCount) {
		this.ovenCount = ovenCount;
	}

	// Test will time out after 1 seconds. If this happens, it is likely caused by
	// deadlock somewhere
	@Rule
	public TestRule timeout = JUnitUtils.createTimeoutRule();

	@Test
	public void test() {
		// Make 100 cakes
		int desiredCakeCount = 100;
		MixedIngredients[] mixes = new MixedIngredients[desiredCakeCount];
		BakedCake[] bakedCakes = new BakedCake[desiredCakeCount];
		IcedCake[] icedCakes = new IcedCake[desiredCakeCount];

		TestMixer mixer = new TestMixer(mixes);

		List<Oven> ovens = new ArrayList<>(ovenCount);
		for (int i = 0; i < ovenCount; ++i) {
			ovens.add(new TestOven(bakedCakes, ovenCount));
		}
		TestIcer icer = new TestIcer(icedCakes);

		// Call the student's solution to bake the cake
		MutableObject<IcedCake[]> result = new MutableObject<>();
		BookkeepingUtils.bookkeep(() -> {
			if (ovenCount == 1) {
				result.setValue(CakePipeline.mixBakeAndIceCakes(mixer, ovens.get(0), icer, desiredCakeCount));
			} else {
				result.setValue(MultiOvenCakePipeline.mixBakeAndIceCakes(mixer, ovens, icer, desiredCakeCount));
			}
		}, (bookkeep) -> {
			assertEquals("Not all the ingredients were mixed", desiredCakeCount, mixer.getCount());
			int totalBakedCakeCount = 0;
			int[] ovenBakeCounts = new int[ovenCount];
			int ovenIndex = 0;
			for (Oven oven : ovens) {
				ovenBakeCounts[ovenIndex] = ((TestOven) oven).getCount();
				totalBakedCakeCount += ovenBakeCounts[ovenIndex];
				ovenIndex++;
			}
			assertEquals("Not all the cakes were baked", desiredCakeCount, totalBakedCakeCount);

			ovenIndex = 0;
			for (Oven oven : ovens) {
				assertThat("oven[" + ovenIndex + "] did not bake any cakes.\noven bake counts: "
						+ Arrays.toString(ovenBakeCounts), ((TestOven) oven).getCount(), is(greaterThan(0)));
				ovenIndex++;
			}

			assertEquals("Not all the cakes were iced", desiredCakeCount, icer.getCount());
			IcedCake[] resultIcedCakes = result.getValue();
			assertEquals("Not enough finished cakes were in the return array", desiredCakeCount,
					resultIcedCakes.length);
			for (int i = 0; i < resultIcedCakes.length; i++) {
				assertNotNull("One of the indicies of the returned cakes was null", resultIcedCakes[i]);
			}
			int finishCount = bookkeep.getNonAccumulatorFinishInvocationCount();
			int taskCount = bookkeep.getTaskCount();

			int expectedTasks = 2 + ovenCount;
			assertThat("Incorrect number of tasks. There should be either " + expectedTasks + " or "
					+ (expectedTasks - 1) + " asyncs called", taskCount,
					either(is(expectedTasks)).or(is(expectedTasks - 1)));
			assertThat("Incorrect number of finishes called. There should only be 0 or 1", finishCount,
					either(is(1)).or(is(0)));

			if (finishCount == 0) {
				assertEquals(
						"There is a potential data race. You either need to have a finish or reduce the number of tasks spanwed.",
						2, taskCount);
				assertSame("The continuation thread is in the wrong location", icer.getThread(),
						Thread.currentThread());
			}
		});
	}
}

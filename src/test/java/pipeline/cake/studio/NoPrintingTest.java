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
package pipeline.cake.studio;

import static edu.wustl.cse231s.v5.V5.launchAppWithReturn;
import static org.junit.Assert.assertNotNull;

import org.junit.Rule;
import org.junit.rules.TestRule;

import edu.wustl.cse231s.junit.JUnitUtils;
import edu.wustl.cse231s.print.AbstractNoPrintingTest;
import pipeline.cake.core.BakedCake;
import pipeline.cake.core.Oven;
import pipeline.cake.core.IcedCake;
import pipeline.cake.core.Icer;
import pipeline.cake.core.MixedIngredients;
import pipeline.cake.core.Mixer;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link CakePipeline#mixBakeAndIceCakes(Mixer, Oven, Icer, int)}
 */
public class NoPrintingTest extends AbstractNoPrintingTest {
	// Test will time out after 1 seconds. If this happens, it is likely caused by
	// deadlock somewhere
	@Rule
	public TestRule timeout = JUnitUtils.createTimeoutRule();

	@Override
	protected void testKernel() {
		int desiredCakeCount = 10;
		Mixer mixer = new Mixer() {

			@Override
			public MixedIngredients mix(int cakeIndex) {
				return new MixedIngredients(cakeIndex);
			}
		};
		Oven oven = new Oven() {
			@Override
			public BakedCake bake(int cakeIndex, MixedIngredients mixedIngredients) {
				return new BakedCake(cakeIndex, mixedIngredients);
			}
		};

		Icer icer = new Icer() {
			@Override
			public IcedCake ice(int cakeIndex, BakedCake bakedCake) {
				return new IcedCake(cakeIndex, bakedCake);
			}
		};
		IcedCake[] icedCakes = launchAppWithReturn(() -> {
			return CakePipeline.mixBakeAndIceCakes(mixer, oven, icer, desiredCakeCount);
		});
		assertNotNull(icedCakes);
	}
}

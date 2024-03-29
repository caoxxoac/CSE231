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
package count.lab;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import edu.wustl.cse231s.bioinformatics.io.resource.ChromosomeResource;
import midpoint.lab.MidpointTest;
import threshold.lab.ThresholdPredicateConstructorTest;
import threshold.lab.ThresholdPredicateTest;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ CountRangeSequentialCorrectnessTest.class, CountSequentialCorrectnessTest.class,
		IsolatedNucleobaseTest.class, MidpointTest.class, NWaySplitRemainderTest.class, LowerUpperTest.class,
		NWaySplitTest.class, NWaySplitRemainderCeilingTest.class, ThresholdPredicateConstructorTest.class,
		ThresholdPredicateTest.class, DivideAndConquerTest.class, LowerUpperParallelismTest.class,
		NWayParallelismTest.class, DivideAndConquerParallelismTest.class, CountNoDeclaredFieldsTest.class,
		NoPrintingTest.class })
public class CountTestSuite {
	@BeforeClass
	public static void setUp() {
		@SuppressWarnings("unused")
		byte[] unused = ChromosomeResource.HOMO_SAPIENS_Y.getData();
	}
}

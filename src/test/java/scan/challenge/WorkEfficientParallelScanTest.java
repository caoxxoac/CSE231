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
package scan.challenge;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.function.IntUnaryOperator;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.wustl.cse231s.junit.JUnitUtils;
import edu.wustl.cse231s.v5.api.Bookkeeping;
import scan.studio.AbstractScanTest;
import scan.util.TestScan;
import scan.util.ValueGenerator;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link WorkEfficientParallelScan#sumScanExclusive(int[])}
 */
@RunWith(Parameterized.class)
public class WorkEfficientParallelScanTest extends AbstractScanTest {
	public WorkEfficientParallelScanTest(IntUnaryOperator valueGenerator, int length) {
		super(valueGenerator, length, new TestScan() {

			@Override
			public int[] sumScan(int[] data) throws InterruptedException, ExecutionException {
				WorkEfficientParallelScan.sumScanExclusiveInPlace(data);
				return data;
			}

			@Override
			public boolean isInclusive() {
				return false;
			}

			@Override
			public boolean isInPlace() {
				return true;
			}
		});
	}

	@Override
	protected void checkParallelism(Bookkeeping bookkeep, int length) {
		// feel free to implement sequentially or in parallel
	}

	@Parameters(name = "{0} length={1}")
	public static Collection<Object[]> getConstructorArguments() {
		return JUnitUtils.toParameterizedArguments2(ValueGenerator.values(), new Integer[] { 8 });
	}
}

/*******************************************************************************
 * Copyright (C) 2016-2017 Dennis Cosgrove
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

package all.studio;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import atomicity.AtomicityTestSuite;
import fibonacci.studio.FibonacciTestSuite;
import floodfill.FloodFillTestSuite;
import iterativeaveraging.studio.IterativeAveragingTestSuite;
import leggedrace.studio.LeggedRaceTestSuite;
import lock.allornothing.studio.TryLockingTestSuite;
import lock.order.studio.LockOrderingTestSuite;
import mapreduce.CholeraStudioTestSuite;
import mapreduce.CollectorStudioTestSuite;
import mapreduce.IntSumStudioTestSuite;
import mapreduce.MutualFriendsStudioTestSuite;
import mapreduce.WindbagStudioTestSuite;
import matrixmultiply.studio.MatrixMultiplyTestSuite;
import pack.studio.PackTestSuite;
import pipeline.cake.studio.CakePipelineTestSuite;
import racecondition.studio.RaceConditionTestSuite;
import scan.studio.ScanTestSuite;
import slice.studio.SlicesTestSuite;
import sort.studio.merge.MergeSortTestSuite;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ SlicesTestSuite.class, MatrixMultiplyTestSuite.class, MergeSortTestSuite.class,
		RaceConditionTestSuite.class, FloodFillTestSuite.class, IntSumStudioTestSuite.class,
		CollectorStudioTestSuite.class, MutualFriendsStudioTestSuite.class, CholeraStudioTestSuite.class,
		WindbagStudioTestSuite.class, FibonacciTestSuite.class, ScanTestSuite.class, PackTestSuite.class,
		AtomicityTestSuite.class, LockOrderingTestSuite.class, TryLockingTestSuite.class,
		IterativeAveragingTestSuite.class, LeggedRaceTestSuite.class, CakePipelineTestSuite.class, })
public class AllStudioTestSuite {
}

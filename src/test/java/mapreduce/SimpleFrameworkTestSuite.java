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
package mapreduce;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import mapreduce.apps.wordcount.core.WordCountUtils;
import mapreduce.framework.lab.simple.AccumulateAllParallelismTest;
import mapreduce.framework.lab.simple.AccumulateAllPointedTest;
import mapreduce.framework.lab.simple.AccumulateAllStressTest;
import mapreduce.framework.lab.simple.FinishAllParallelismTest;
import mapreduce.framework.lab.simple.FinishAllPointedTest;
import mapreduce.framework.lab.simple.FinishAllStressTest;
import mapreduce.framework.lab.simple.MapAllParallelismTest;
import mapreduce.framework.lab.simple.MapAllPointedTest;
import mapreduce.framework.lab.simple.MapAllStressTest;
import mapreduce.framework.lab.simple.SimpleFrameworkStressTest;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ MapAllPointedTest.class, MapAllStressTest.class, AccumulateAllPointedTest.class,
		AccumulateAllStressTest.class, FinishAllPointedTest.class, FinishAllStressTest.class,
		SimpleFrameworkStressTest.class, MapAllParallelismTest.class, AccumulateAllParallelismTest.class,
		FinishAllParallelismTest.class })
public class SimpleFrameworkTestSuite {
	@BeforeClass
	public static void setUp() throws IOException {
		WordCountUtils.downloadWordResources();
	}
}

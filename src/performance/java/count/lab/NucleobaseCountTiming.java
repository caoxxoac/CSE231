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

import static edu.wustl.cse231s.v5.V5.launchApp;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import count.core.NucleobaseCountUtils;
import edu.wustl.cse231s.NotYetImplementedException;
import edu.wustl.cse231s.bioinformatics.Nucleobase;
import edu.wustl.cse231s.bioinformatics.io.resource.ChromosomeResource;
import edu.wustl.cse231s.timing.ImmutableTimer;
import threshold.lab.GreaterThanThresholdPredicate;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class NucleobaseCountTiming {
	private static String FORMAT = "%60s";

	private static void timeCountSequential(byte[] chromosome, Nucleobase targetNucleobase, int expectedCount) {
		try {
			ImmutableTimer timer = new ImmutableTimer(String.format(FORMAT, "NucleobaseCounting.countSequential"));
			int count = NucleobaseCounting.countSequential(chromosome, targetNucleobase);
			long dt = timer.mark();
			if (count == expectedCount) {
				timer.printResults(dt);
			} else {
				throw new RuntimeException(count + " does not equal expect count " + expectedCount);
			}
		} catch (NotYetImplementedException nyie) {
			System.out.println("NucleobaseCounting.countSequential: NOT YET IMPLEMENTED");
		}
	}

	private static void timeCountParallelUpperLower(byte[] chromosome, Nucleobase targetNucleobase, int expectedCount)
			throws InterruptedException, ExecutionException {
		try {
			ImmutableTimer timer = new ImmutableTimer(
					String.format(FORMAT, "NucleobaseCounting.countParallelUpperLowerSplit"));
			int count = NucleobaseCounting.countParallelLowerUpperSplit(chromosome, targetNucleobase);
			long dt = timer.mark();
			if (count == expectedCount) {
				timer.printResults(dt);
			} else {
				throw new RuntimeException(count + " does not equal expect count " + expectedCount);
			}
		} catch (NotYetImplementedException nyie) {
			System.out.println("NucleobaseCounting.countParallelLowerUpperSplit: NOT YET IMPLEMENTED");
		}
	}

	private static void timeCountParallelNWaySplit(byte[] chromosome, Nucleobase targetNucleobase, int numTasks,
			int expectedCount) throws InterruptedException, ExecutionException {
		try {
			ImmutableTimer timer = new ImmutableTimer(
					String.format(FORMAT, "NucleobaseCounting.countParallelNWaySplit(" + numTasks + ")"));
			int count = NucleobaseCounting.countParallelNWaySplit(chromosome, targetNucleobase, numTasks);
			long dt = timer.mark();
			if (count == expectedCount) {
				timer.printResults(dt);
			} else {
				throw new RuntimeException(count + " does not equal expect count " + expectedCount);
			}
		} catch (NotYetImplementedException nyie) {
			System.out.println("NucleobaseCounting.countParallelNWaySplit: NOT YET IMPLEMENTED");
		}
	}

	private static void timeCountDivideAndConquer(byte[] chromosome, Nucleobase targetNucleobase,
			GreaterThanThresholdPredicate isParallelRangeLengthPredicate, int expectedCount)
			throws InterruptedException, ExecutionException {
		try {
			ImmutableTimer timer = new ImmutableTimer(
					String.format(FORMAT, "NucleobaseCounting.countParallelDivideAndConquer("
							+ isParallelRangeLengthPredicate.getThreshold() + ")"));
			int count = NucleobaseCounting.countParallelDivideAndConquer(chromosome, targetNucleobase,
					isParallelRangeLengthPredicate);
			long dt = timer.mark();
			if (count == expectedCount) {
				timer.printResults(dt);
			} else {
				throw new RuntimeException(count + " does not equal expect count " + expectedCount);
			}
		} catch (NotYetImplementedException nyie) {
			System.out.println("NucleobaseCounting.countParallelDivideAndConquer: NOT YET IMPLEMENTED");
		}
	}

	public static void main(String[] args) throws Exception {
		for (ChromosomeResource chromosomeResource : Arrays.asList(ChromosomeResource.HOMO_SAPIENS_1)) {
			byte[] chromosome = chromosomeResource.getData();

			Nucleobase targetNucleobase = Nucleobase.ADENINE;
			int expectedCount = NucleobaseCountUtils.countSequential(chromosome, targetNucleobase);

			int numProcessors = Runtime.getRuntime().availableProcessors();
			launchApp(() -> {
				final int ITERATION_COUNT = 10;
				for (int iteration = 0; iteration < ITERATION_COUNT; iteration++) {

					timeCountSequential(chromosome, targetNucleobase, expectedCount);

					timeCountParallelUpperLower(chromosome, targetNucleobase, expectedCount);

					timeCountParallelNWaySplit(chromosome, targetNucleobase, numProcessors, expectedCount);
					timeCountParallelNWaySplit(chromosome, targetNucleobase, numProcessors * 2, expectedCount);
					timeCountParallelNWaySplit(chromosome, targetNucleobase, numProcessors * 10, expectedCount);
					timeCountParallelNWaySplit(chromosome, targetNucleobase, numProcessors * 100, expectedCount);

					timeCountDivideAndConquer(chromosome, targetNucleobase,
							new GreaterThanThresholdPredicate(chromosome.length / 2), expectedCount);
					timeCountDivideAndConquer(chromosome, targetNucleobase,
							new GreaterThanThresholdPredicate(chromosome.length / 4), expectedCount);
					timeCountDivideAndConquer(chromosome, targetNucleobase,
							new GreaterThanThresholdPredicate(chromosome.length / 10), expectedCount);
					timeCountDivideAndConquer(chromosome, targetNucleobase,
							new GreaterThanThresholdPredicate(chromosome.length / 71), expectedCount);

					System.out.println();
				}
			});
		}

	}

}

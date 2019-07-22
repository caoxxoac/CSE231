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

package iterativeaveraging.studio;

import static edu.wustl.cse231s.v5.V5.forall;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Phaser;

import edu.wustl.cse231s.NotYetImplementedException;
import edu.wustl.cse231s.phasable.PhasableDoubleArrays;
import iterativeaveraging.core.IterativeAverager;
import iterativeaveraging.optional.IterativeAveragingRangeUtils;
import range.core.Range;
import range.core.Ranges;

/**
 * @author Xiangzhi Cao
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class FuzzyPhasedParallelIterativeAverager implements IterativeAverager {
	private final int sliceCount;

	public FuzzyPhasedParallelIterativeAverager(int sliceCount) {
		this.sliceCount = sliceCount;
	}

	@Override
	public double[] iterativelyAverage(double[] originalArray, int iterationCount)
			throws InterruptedException, ExecutionException {
		int length = originalArray.length;
		PhasableDoubleArrays pda = new PhasableDoubleArrays(originalArray);

		Phaser[] phasers = new Phaser[length];
		List<Range> ranges = Ranges.slice(0, length, this.sliceCount);
		for (int i=0; i<this.sliceCount; i++) {
			phasers[i] = new Phaser();
			phasers[i].register();
		}

		forall(0, this.sliceCount, (index)->{
			for (int i=0; i<iterationCount; i++) {
				for (int j=ranges.get(index).getMinInclusive(); j<ranges.get(index).getMaxExclusive(); j++) {
					if (j == 0) {
						pda.getDstForPhase(i)[j] = pda.getSrcForPhase(i)[j];
					}
					else if (j == length-1) {
						pda.getDstForPhase(i)[j] = pda.getSrcForPhase(i)[j];
					}
					else {
						pda.getDstForPhase(i)[j] = (pda.getSrcForPhase(i)[j-1] + pda.getSrcForPhase(i)[j+1]) / 2;
					}
				}
				phasers[index].arrive();
				for (int j=0; j<this.sliceCount; j++) {
					phasers[j].awaitAdvance(i);
				}
			}
		});
		return pda.getSrcForPhase(iterationCount);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}

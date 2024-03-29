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
package leggedrace.studio;

import static edu.wustl.cse231s.v5.V5.forall;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Phaser;

import edu.wustl.cse231s.NotYetImplementedException;
import edu.wustl.cse231s.util.IntegerRange;
import leggedrace.core.LeggedRace;
import leggedrace.core.Participant;

/**
 * @author Xiangzhi Cao
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class ForallPhasedLeggedRace implements LeggedRace {
	/**
	 * The parallel solution to the simulated "n+1"-legged race which does make use
	 * of phasers, but only one who all the participants are registered to and will
	 * await on.
	 */
	@Override
	public void takeSteps(Participant[] participants, int stepCount) throws InterruptedException, ExecutionException {
		Phaser phaser = new Phaser();
		phaser.bulkRegister(participants.length);
		
		forall(participants, (participant)->{
			for (int step=0; step<stepCount; step++) {
				participant.takeStep(step);
				phaser.arriveAndAwaitAdvance();
			}
		});
	}
}

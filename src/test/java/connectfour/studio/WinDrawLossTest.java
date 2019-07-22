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
package connectfour.studio;

import static edu.wustl.cse231s.v5.V5.launchApp;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import connectfour.core.BitBoard;
import connectfour.core.Board;
import connectfour.core.EvaluationColumnPair;
import connectfour.core.Outcome;
import connectfour.core.PositionExpectedScorePair;
import connectfour.core.PositionExpectedScorePairs;

@RunWith(Parameterized.class)
public class WinDrawLossTest {
	private final PositionExpectedScorePair positionExpectedScorePair;

	public WinDrawLossTest(PositionExpectedScorePair positionExpectedScorePair) {
		this.positionExpectedScorePair = positionExpectedScorePair;
	}

	@Test
	public void test() throws InterruptedException, ExecutionException {
		Board board = new BitBoard(this.positionExpectedScorePair.getPosition());
		int maxDepth = 42 - board.getTurnsPlayed();
		launchApp(() -> {
			EvaluationColumnPair columnEvaluationPair = ConnectFour.negamaxKernel(board, WinOrLoseHeuristic.INSTANCE,
					(depth) -> depth < maxDepth, (depth) -> depth < 2, 0);
			Outcome expectedWinDrawLoss = Outcome.toOutcome(positionExpectedScorePair);
			Outcome actualWinDrawLoss = Outcome.toOutcome(columnEvaluationPair);
			assertEquals(expectedWinDrawLoss, actualWinDrawLoss);
		});
	}

	@Parameters(name = "{0}")
	public static Collection<Object[]> getConstructorArguments() throws MalformedURLException, IOException {
		List<PositionExpectedScorePair> positionExpectedScorePairs = PositionExpectedScorePairs.readAll("Test_L3_R1");

		positionExpectedScorePairs = positionExpectedScorePairs.subList(0, 71);

		List<Object[]> result = new ArrayList<>(positionExpectedScorePairs.size());
		for (PositionExpectedScorePair positionExpectedScorePair : positionExpectedScorePairs) {
			result.add(new Object[] { positionExpectedScorePair });
		}
		return result;
	}
}

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
package connectfour.studio;

import static edu.wustl.cse231s.v5.V5.forall;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.IntPredicate;
import java.util.function.ToDoubleFunction;

import connectfour.core.Board;
import connectfour.core.EvaluationColumnPair;
import edu.wustl.cse231s.NotYetImplementedException;

/**
 * @author Xiangzhi Cao
 * @author Finn Voichick
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class ConnectFour {
	/**
	 * @param evaluation
	 *            the specified evaluation
	 * @return a new EvaluationColumnPair with an empty column
	 */
	private static EvaluationColumnPair createEvaluationOnly(double evaluation) {
		return new EvaluationColumnPair(evaluation, Optional.empty());
	}

	/**
	 * Selects the maximally negative of the opposing player's evaluations held in
	 * nextDepthPairs. Null array elements should be ignored.
	 * 
	 * @param nextDepthPairs
	 *            the evaluation column pairs of the opposing player
	 * @return the selected evaluation and column pair
	 */
	private static EvaluationColumnPair select(EvaluationColumnPair[] nextDepthPairs) {
		EvaluationColumnPair currentMax = null;
		double max = Double.MIN_VALUE;
		for (EvaluationColumnPair nextDepthPair : nextDepthPairs) {
			double current = nextDepthPair.getEvaluation();
			if (current > max) {
				max = current;
				currentMax = nextDepthPair;
			}
		}
		return currentMax;
	}

	/**
	 * @param board
	 *            the board state
	 * @param heuristic
	 *            the heuristic function to evaluate a board state
	 * @param searchAtDepth
	 *            predicate to test whether to continue searching (true case) or to
	 *            evaluate (false case)
	 * @param parallelAtDepth
	 *            predicate to test whether to create parallel tasks (true case) or
	 *            to fall back to sequential execution (false case)
	 * @param currentDepth
	 *            current depth of this search which is used to test the predicates.
	 * @return
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	static EvaluationColumnPair negamaxKernel(Board board, ToDoubleFunction<Board> heuristic,
			IntPredicate searchAtDepth, IntPredicate parallelAtDepth, int currentDepth)
					throws InterruptedException, ExecutionException {
		EvaluationColumnPair[] evaColPairs = new EvaluationColumnPair[board.getValidPlays().size()];
		if (searchAtDepth.test(currentDepth)) {
			if (parallelAtDepth.negate().test(currentDepth)) {
				forall(board.getValidPlays(), (play)->{
					Board tempBoard = board.createNextBoard(play);
					double value = heuristic.applyAsDouble(tempBoard);
					evaColPairs[play] = createEvaluationOnly(value);
				});
			}
			// if cannot do it in parallel, just do it in sequential
			else {
				for (Integer play : board.getValidPlays()) {
					Board tempBoard = board.createNextBoard(play);
					double value = heuristic.applyAsDouble(tempBoard);
					evaColPairs[play] = createEvaluationOnly(value);
				}
			}
		}
		return select(evaColPairs);
	}

	/**
	 * Given a specified board state, heuristic function, searchAtDepth predicate,
	 * and parallelAtDepth predicate select which column is best for the next move.
	 * 
	 * @param board
	 *            the board state
	 * @param heuristic
	 *            the heuristic function to evaluate
	 * @param searchAtDepth
	 *            predicate to test whether to continue searching (true case) or to
	 *            evaluate (false case)
	 * @param parallelAtDepth
	 *            predicate to test whether to create parallel tasks (true case) or
	 *            to fall back to sequential execution (false case)
	 * @return the chosen best column index for the next move
	 * @throws IllegalStateException
	 *             if no move can be made
	 * @throws InterruptedException
	 *             if the computation was cancelled
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	public static int selectNextColumn(Board board, ToDoubleFunction<Board> heuristic, IntPredicate searchAtDepth,
			IntPredicate parallelAtDepth) throws InterruptedException, ExecutionException {
		double max = Double.MIN_VALUE;
		int col = 0;
		for (Integer play : board.getValidPlays()) {
			Board tempBoard = board.createNextBoard(play);
			double value = heuristic.applyAsDouble(tempBoard);
			if (value > max) {
				max = value;
				col = play;
			}
		}
		return col;
	}
}

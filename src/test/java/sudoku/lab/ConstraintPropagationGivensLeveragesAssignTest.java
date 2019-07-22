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

package sudoku.lab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Map;
import java.util.SortedSet;

import org.junit.Test;

import backtrack.lab.rubric.BacktrackRubric;
import sudoku.core.ConstraintPropagator;
import sudoku.core.Square;
import sudoku.core.io.PuzzlesResourceUtils;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
@BacktrackRubric(BacktrackRubric.Category.CONTRAINT_PROPAGATOR)
public class ConstraintPropagationGivensLeveragesAssignTest {
	@Test
	public void test() {
		// source: http://norvig.com/hardest.txt
		String givens = "85...24..72......9..4.........1.7..23.5...9...4...........8..7..17..........36.4.";
		ConstraintPropagator constraintPropagator = new DefaultConstraintPropagator();
		Map<Square, SortedSet<Integer>> map = constraintPropagator.createOptionSetsFromGivens(givens);

		int[][] values = PuzzlesResourceUtils.parseGivens(givens);
		for (int row = 0; row < values.length; row++) {
			for (int column = 0; column < values[row].length; column++) {
				Square square = Square.valueOf(row, column);
				SortedSet<Integer> options = map.get(square);
				int value = values[row][column];
				if (value != 0) {
					assertEquals("\nsquare " + square + " was given.\nThe size of its option set should be 1.\n", 1,
							options.size());
					assertEquals("\nsquare " + square + " was given.\nThe only value in its option set should be "
							+ value + ".\n", value, (int) options.first());
				} else {
					assertNotEquals("\nAlthough square " + square
							+ " was not given, the assignment of some of its peers should have caused some of its options to be eliminated.\nInstead, all of its options remain.\nOption set size: ",
							9, options.size());
				}
			}
		}
	}
}

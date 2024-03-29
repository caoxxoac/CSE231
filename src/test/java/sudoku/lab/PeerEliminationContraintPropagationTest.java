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
package sudoku.lab;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import backtrack.lab.rubric.BacktrackRubric;
import sudoku.AbstractContraintPropagationTest;
import sudoku.core.io.PuzzlesResource;
import sudoku.core.io.PuzzlesResourceUtils;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link DefaultConstraintPropagator}
 */
@BacktrackRubric(BacktrackRubric.Category.CONTRAINT_PROPAGATOR)
@RunWith(Parameterized.class)
public class PeerEliminationContraintPropagationTest extends AbstractContraintPropagationTest {
	public PeerEliminationContraintPropagationTest(String givens) {
		super(givens);
	}

	@Parameters(name = "{0}")
	public static Collection<Object[]> getConstructorArguments() throws IOException {
		List<String> easyGivensList = PuzzlesResourceUtils.readGivens(PuzzlesResource.EASY50);
		// This list of numbers represents the puzzles that should be able to be
		// completely solved just through Peer Elimination Constraint Propagation
		List<Integer> completelyConstrainableWithPeerEliminationOnlyIndices = Arrays.asList(0, 4, 7, 11, 15, 16, 18, 19,
				33, 35, 37, 39);

		Collection<Object[]> results = new LinkedList<>();
		for (int i : completelyConstrainableWithPeerEliminationOnlyIndices) {
			// This calls the AbstractContraintPropagationTest for each of the puzzles. The
			// abstract test checks to make sure the entire puzzle is completed just by
			// constructing the DefaultImmutableSudokuPuzzle from the givens.
			results.add(new Object[] { easyGivensList.get(i) });
		}
		return results;
	}
}

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

package nqueens.lab;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import backtrack.lab.rubric.BacktrackRubric;
import edu.wustl.cse231s.junit.JUnitUtils;
import nqueens.core.DefaultMutableQueenLocations;
import nqueens.core.MutableQueenLocations;
import nqueens.core.NQueensCorrectnessUtils;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link SequentialNQueens#countSolutions(MutableQueenLocations)}
 */
@RunWith(Parameterized.class)
@BacktrackRubric(BacktrackRubric.Category.SEQUENTIAL_N_QUEENS)
public class SequentialNQueensCorrectnessTest extends AbstractNQueensCorrectnessTest {
	public SequentialNQueensCorrectnessTest(int boardSize) throws IOException {
		super(boardSize);
	}

	@Override
	protected int countSolutions(int boardSize) throws InterruptedException, ExecutionException {
		MutableQueenLocations queenLocations = new DefaultMutableQueenLocations(boardSize);
		return SequentialNQueens.countSolutions(queenLocations);
	}

	@Parameters(name = "boardSize={0}")
	public static Collection<Object[]> getConstructorArguments() {
		return JUnitUtils.toParameterizedArguments(NQueensCorrectnessUtils.getBoardSizes());
	}
}

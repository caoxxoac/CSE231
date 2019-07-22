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

import java.util.Collection;
import java.util.LinkedList;

import edu.wustl.cse231s.NotYetImplementedException;
import nqueens.core.ImmutableQueenLocations;
import nqueens.core.QueenLocations;

/**
 * @author Xiangzhi Cao
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class QueenLocationsUtils {
	public static Collection<Integer> getCandidateColumns(QueenLocations queenLocations, int row) {
		Collection<Integer> cols = new LinkedList<Integer>();
		for (int column=0; column<queenLocations.getBoardSize(); column++){
			if (queenLocations.isCandidateThreatFree(row, column)) {
				cols.add(column);
			}
		}
		return cols;
	}

	public static Collection<Integer> getCandidateColumnsForNextRow(ImmutableQueenLocations queenLocations) {
		int row = queenLocations.getRowCount();
		return getCandidateColumns(queenLocations, row);
	}
}

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

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

import edu.wustl.cse231s.NotYetImplementedException;
import sudoku.core.ConstraintPropagator;
import sudoku.core.OptionSet;
import sudoku.core.OptionSetUtils;
import sudoku.core.Square;
import sudoku.core.SquareToOptionSetMapUtils;
import sudoku.core.Units;
import sudoku.core.io.PuzzlesResourceUtils;

/**
 * @author Xiangzhi Cao
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class DefaultConstraintPropagator implements ConstraintPropagator {
	@Override
	public Map<Square, SortedSet<Integer>> createOptionSetsFromGivens(String givens) {
		HashMap<Square, SortedSet<Integer>> map = new HashMap<Square, SortedSet<Integer>>();
		int[][] values = PuzzlesResourceUtils.parseGivens(givens);

		Square[] puzzle = Square.values();
		for (Square s : puzzle) {
			map.put(s, OptionSet.createAllOptions());
		}
		for (int row=0; row<values.length; row++) {
			for (int col=0; col<values[row].length; col++) {
				int currentValue = values[row][col];
				if (currentValue != 0) {
					Square currentSquare = Square.valueOf(row, col);
					assign(map, currentSquare, currentValue);
				}
			}
		}
		return map;
	}

	@Override
	public Map<Square, SortedSet<Integer>> createNextOptionSets(Map<Square, SortedSet<Integer>> otherOptionSets,
			Square square, int value) {
		//		for (Square s : otherOptionSets.keySet()) {
		//			map.put(s, otherOptionSets.get(s));
		//		}
		Map<Square, SortedSet<Integer>> map = SquareToOptionSetMapUtils.deepCopyOf(otherOptionSets);

		assign(map, square, value);

		return map;
	}

	/**
	 * @throws IllegalArgumentException
	 *             if the given value is not an option for the given square
	 */
	private void assign(Map<Square, SortedSet<Integer>> resultOptionSets, Square square, int value) {
		SortedSet<Integer> copy = OptionSet.copyOf(resultOptionSets.get(square));
		//System.out.println(resultOptionSets.get(square) + " "+ value +" "+copy.contains(value));

		if (!copy.contains(value)) {
			throw new IllegalArgumentException();
		}
		resultOptionSets.put(square, OptionSet.createSingleOption(value));

		eliminate(resultOptionSets, square, value);

	}

	private void eliminate(Map<Square, SortedSet<Integer>> resultOptionSets, Square square, int value) {
		for (Square s : square.getPeers()) {

			SortedSet<Integer> temp = resultOptionSets.get(s);
			if (temp.contains(value)) {
				if (temp.size() == 0) {
					return;
				}
				else if (temp.size() == 1) {
					temp.remove(value);
					return;
				}
				else if (temp.size() == 2){
					temp.remove(value);
					assign(resultOptionSets, s, temp.first());
				}
				else {
					temp.remove(value);
				}
			}
		}

	}
}

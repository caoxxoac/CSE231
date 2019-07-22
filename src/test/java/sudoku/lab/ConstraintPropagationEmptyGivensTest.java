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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import backtrack.lab.rubric.BacktrackRubric;
import sudoku.core.ConstraintPropagator;
import sudoku.core.Square;
import sudoku.util.GivensUtils;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
@BacktrackRubric(BacktrackRubric.Category.CONTRAINT_PROPAGATOR)
public class ConstraintPropagationEmptyGivensTest {
	@Test
	public void test() {
		ConstraintPropagator constraintPropagator = new DefaultConstraintPropagator();
		String givens = GivensUtils.createEmptyGivens();
		Map<Square, SortedSet<Integer>> map = constraintPropagator.createOptionSetsFromGivens(givens);
		assertNotNull(map);
		assertEquals("Your map is not the right size. It should have entries for the 81 squares", 81, map.size());
		SortedSet<Integer> expectedSet = new TreeSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
		for (Square square : Square.values()) {
			SortedSet<Integer> actualSet = map.get(square);
			assertNotNull(actualSet);
			assertEquals(
					"If a square isn't given a specific value in the givens, you should put all 9 options in its set",
					expectedSet, actualSet);
		}

		Collection<SortedSet<Integer>> collection = map.values();
		@SuppressWarnings("unchecked")
		SortedSet<Integer>[] array = new SortedSet[collection.size()];
		collection.toArray(array);
		for (int i = 0; i < array.length; ++i) {
			for (int j = i + 1; j < array.length; ++j) {
				assertNotSame("\nEach entry in the map should have its own separate option set.\nFound shared sets.\n", array[i], array[j]);
			}
		}

	}
}

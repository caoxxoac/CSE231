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

package range.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class Ranges {
	public static List<Range> slice(int min, int maxExclusive, int numSlices) {
		List<Range> result = new ArrayList<>(numSlices);
		int range = maxExclusive - min;
		int indicesPerSlice = range / numSlices;
		int remainder = range % numSlices;
		int sliceMin = min;
		for (int sliceId = 0; sliceId < numSlices; sliceId++) {
			int sliceMax = sliceMin + indicesPerSlice;
			if (sliceId < remainder) {
				sliceMax++;
			}
			result.add(new Range(sliceId, sliceMin, sliceMax));
			sliceMin = sliceMax;
		}
		return result;
	}
}

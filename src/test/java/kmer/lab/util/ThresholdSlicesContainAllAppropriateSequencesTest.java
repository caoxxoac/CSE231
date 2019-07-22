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
package kmer.lab.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import edu.wustl.cse231s.bioinformatics.io.resource.ChromosomeResource;
import edu.wustl.cse231s.junit.JUnitUtils;
import kmer.lab.rubric.KMerRubric;
import slice.core.Slice;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link ThresholdSlices#createSlicesBelowThreshold(List, int, int)}
 */
@KMerRubric(KMerRubric.Category.THRESHOLD_SLICES)
public class ThresholdSlicesContainAllAppropriateSequencesTest {
	private static List<byte[]> yChromosome;

	@Rule
	public TestRule timeout = JUnitUtils.createTimeoutRule();

	@BeforeClass
	public static void setUp() throws IOException {
		yChromosome = ChromosomeResource.readSubSequences(ChromosomeResource.HOMO_SAPIENS_Y);
	}

	@Test
	public void test() throws IOException {
		assertNotNull(yChromosome);
		int k = 9;
		int threshold = 1_000_000;
		List<Slice<byte[]>> slices = ThresholdSlices.createSlices(yChromosome, k, (range) -> range > threshold);

		Set<byte[]> sequencesFoundInSlices = new HashSet<>();
		slices.forEach((slice) -> {
			sequencesFoundInSlices.add(slice.getOriginalUnslicedData());
		});

		for (byte[] sequence : yChromosome) {
			if (sequence.length >= k) {
				assertTrue(sequencesFoundInSlices.contains(sequence));
			}
		}
	}
}

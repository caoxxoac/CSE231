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
package kmer;

import static edu.wustl.cse231s.v5.V5.launchAppWithReturn;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import edu.wustl.cse231s.junit.JUnitUtils;
import edu.wustl.cse231s.v5.AbstractV5Test;
import kmer.core.KMerCount;
import kmer.core.KMerCounter;
import kmer.util.KMerResource;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public abstract class AbstractKMerPreliminaryTest extends AbstractV5Test {
	@Rule
	public TestRule timeout = JUnitUtils.createTimeoutRule();

	protected abstract KMerCounter createKMerCounter();

	protected abstract Class<? extends KMerCount> getKMerCountClass();

	@Test
	public void test() {
		List<byte[]> sequences = KMerResource.CHOLERAE_ORI_C.getSubSequences();
		KMerCount count = launchAppWithReturn(() -> {
			return createKMerCounter().parse(sequences, 3);
		});
		assertNotNull(count);
		assertThat(count, CoreMatchers.instanceOf(getKMerCountClass()));
		assertEquals(4, count.getCount(toBytes("AAA")));
		assertEquals(6, count.getCount(toBytes("TAA")));
		assertEquals(12, count.getCount(toBytes("CAA")));
		assertEquals(7, count.getCount(toBytes("GAA")));
		assertEquals(7, count.getCount(toBytes("ATA")));
		assertEquals(10, count.getCount(toBytes("TTA")));
		assertEquals(3, count.getCount(toBytes("CTA")));
		assertEquals(4, count.getCount(toBytes("GTA")));
		assertEquals(7, count.getCount(toBytes("ACA")));
		assertEquals(17, count.getCount(toBytes("TCA")));
		assertEquals(8, count.getCount(toBytes("CCA")));
		assertEquals(3, count.getCount(toBytes("GCA")));
		assertEquals(8, count.getCount(toBytes("AGA")));
		assertEquals(25, count.getCount(toBytes("TGA")));
		assertEquals(7, count.getCount(toBytes("CGA")));
		assertEquals(7, count.getCount(toBytes("GGA")));
		assertEquals(10, count.getCount(toBytes("AAT")));
		assertEquals(6, count.getCount(toBytes("TAT")));
		assertEquals(16, count.getCount(toBytes("CAT")));
		assertEquals(21, count.getCount(toBytes("GAT")));
		assertEquals(11, count.getCount(toBytes("ATT")));
		assertEquals(16, count.getCount(toBytes("TTT")));
		assertEquals(17, count.getCount(toBytes("CTT")));
		assertEquals(11, count.getCount(toBytes("GTT")));
		assertEquals(9, count.getCount(toBytes("ACT")));
		assertEquals(16, count.getCount(toBytes("TCT")));
		assertEquals(9, count.getCount(toBytes("CCT")));
		assertEquals(10, count.getCount(toBytes("GCT")));
		assertEquals(2, count.getCount(toBytes("AGT")));
		assertEquals(10, count.getCount(toBytes("TGT")));
		assertEquals(5, count.getCount(toBytes("CGT")));
		assertEquals(4, count.getCount(toBytes("GGT")));
		assertEquals(3, count.getCount(toBytes("AAC")));
		assertEquals(7, count.getCount(toBytes("TAC")));
		assertEquals(5, count.getCount(toBytes("CAC")));
		assertEquals(13, count.getCount(toBytes("GAC")));
		assertEquals(21, count.getCount(toBytes("ATC")));
		assertEquals(12, count.getCount(toBytes("TTC")));
		assertEquals(14, count.getCount(toBytes("CTC")));
		assertEquals(1, count.getCount(toBytes("GTC")));
		assertEquals(5, count.getCount(toBytes("ACC")));
		assertEquals(7, count.getCount(toBytes("TCC")));
		assertEquals(1, count.getCount(toBytes("CCC")));
		assertEquals(8, count.getCount(toBytes("GCC")));
		assertEquals(10, count.getCount(toBytes("AGC")));
		assertEquals(8, count.getCount(toBytes("TGC")));
		assertEquals(4, count.getCount(toBytes("CGC")));
		assertEquals(3, count.getCount(toBytes("GGC")));
		assertEquals(12, count.getCount(toBytes("AAG")));
		assertEquals(5, count.getCount(toBytes("TAG")));
		assertEquals(2, count.getCount(toBytes("CAG")));
		assertEquals(6, count.getCount(toBytes("GAG")));
		assertEquals(15, count.getCount(toBytes("ATG")));
		assertEquals(17, count.getCount(toBytes("TTG")));
		assertEquals(10, count.getCount(toBytes("CTG")));
		assertEquals(5, count.getCount(toBytes("GTG")));
		assertEquals(7, count.getCount(toBytes("ACG")));
		assertEquals(7, count.getCount(toBytes("TCG")));
		assertEquals(3, count.getCount(toBytes("CCG")));
		assertEquals(4, count.getCount(toBytes("GCG")));
		assertEquals(5, count.getCount(toBytes("AGG")));
		assertEquals(4, count.getCount(toBytes("TGG")));
		assertEquals(5, count.getCount(toBytes("CGG")));
		assertEquals(1, count.getCount(toBytes("GGG")));

		// count.forEach((a) -> {
		// System.out.println("assertEquals(" + count.getCount(a) + ",
		// count.getCount(toBytes(\"" + new String(a)
		// + "\")));");
		// });
	}

	private static byte[] toBytes(String s) {
		return s.getBytes(StandardCharsets.US_ASCII);
	}

}

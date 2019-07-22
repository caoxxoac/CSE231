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

package edu.wustl.cse231s.bioinformatics.io.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.commons.io.FilenameUtils;

import edu.wustl.cse231s.bioinformatics.io.FastaSplitUtils;
import edu.wustl.cse231s.bioinformatics.io.FastaUtils;
import edu.wustl.cse231s.download.DownloadUtils;
import edu.wustl.cse231s.lazy.Lazy;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public enum ChromosomeResource {
	ALL_ADENINE_10(() -> {
		return "AAAAAAAAAA".getBytes();
	}), MITOCHONDRION(() -> {
		InputStream is = ChromosomeResource.class.getResourceAsStream("HomoSapiensMitochondrion.fasta");
		Objects.requireNonNull(is);
		try {
			return FastaUtils.read(is);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}), CHOLERAE_ORI_C(() -> {
		StringBuilder sb = new StringBuilder();
		sb.append("atcaatgatcaacgtaagcttctaagcatgatcaaggtgctcacacagtttatccacaac");
		sb.append("ctgagtggatgacatcaagataggtcgttgtatctccttcctctcgtactctcatgacca");
		sb.append("cggaaagatgatcaagagaggatgatttcttggccatatcgcaatgaatacttgtgactt");
		sb.append("gtgcttccaattgacatcttcagcgccatattgcgctggccaaggtgacggagcgggatt");
		sb.append("acgaaagcatgatcatggctgttgttctgtttatcttgttttgactgagacttgttagga");
		sb.append("tagacggtttttcatcactgactagccaaagccttactctgcctgacatcgaccgtaaat");
		sb.append("tgataatgaatttacatgcttccgcgacgatttacctcttgatcatcgatccgattgaag");
		sb.append("atcttcaattgttaattctcttgcctcgactcatagccatgatgagctcttgatcatgtt");
		sb.append("tccttaaccctctattttttacggaagaatgatcaagctgctgctcttgatcatcgtttc");

		String text = sb.toString().toUpperCase();
		return text.getBytes();
	}), HOMO_SAPIENS_1, HOMO_SAPIENS_2, HOMO_SAPIENS_3, HOMO_SAPIENS_4, HOMO_SAPIENS_5, HOMO_SAPIENS_6, HOMO_SAPIENS_7, HOMO_SAPIENS_8, HOMO_SAPIENS_9, HOMO_SAPIENS_10, HOMO_SAPIENS_11, HOMO_SAPIENS_12, HOMO_SAPIENS_13, HOMO_SAPIENS_14, HOMO_SAPIENS_15, HOMO_SAPIENS_16, HOMO_SAPIENS_17, HOMO_SAPIENS_18, HOMO_SAPIENS_19, HOMO_SAPIENS_20, HOMO_SAPIENS_21, HOMO_SAPIENS_22, HOMO_SAPIENS_X, HOMO_SAPIENS_Y;

	private final Lazy<byte[]> lazy;

	private ChromosomeResource(Supplier<byte[]> initializer) {
		this.lazy = new Lazy<>(initializer);
	}

	private ChromosomeResource() {
		this.lazy = new Lazy<>(() -> {
			URL url = this.getHomoSapiensURL();
			try {
				File file = DownloadUtils.getDownloadedFile(url);
				Objects.requireNonNull(file);
				return FastaUtils.read(file);
			} catch (IOException ioe) {
				throw new RuntimeException(url.toExternalForm(), ioe);
			}
		});
	}

	public byte[] getData() {
		return this.lazy.get();
	}

	public static Iterable<ChromosomeResource> getTestEnumConstants() {
		return Arrays.asList(ALL_ADENINE_10, MITOCHONDRION, CHOLERAE_ORI_C, HOMO_SAPIENS_Y);
	}

	private URL getHomoSapiensURL() {
		String subPath = name().substring("HOMO_SAPIENS_".length(), name().length());
		String spec = "ftp://ftp.ncbi.nlm.nih.gov/genomes/all/GCA/000/001/405/GCA_000001405.25_GRCh38.p10/GCA_000001405.25_GRCh38.p10_assembly_structure/Primary_Assembly/assembled_chromosomes/FASTA/chr"
				+ subPath + ".fna.gz";
		try {
			return new URL(spec);
		} catch (MalformedURLException murle) {
			throw new Error(spec, murle);
		}
	}

	public static List<byte[]> readSubSequences(ChromosomeResource chromosomeResource) throws IOException {
		File downloadsDirectory = DownloadUtils.getDownloadsDirectory();

		URL url = chromosomeResource.getHomoSapiensURL();
		String filename = FilenameUtils.getName(url.getPath());
		File sequencesFile = new File(downloadsDirectory, filename.replaceAll(".fna.gz", ".subSequences.zip"));
		List<byte[]> originalSequences;
		if (sequencesFile.exists()) {
			originalSequences = FastaSplitUtils.readSubSequences(sequencesFile);
		} else {
			byte[] chromosome = chromosomeResource.getData();
			originalSequences = FastaSplitUtils.splitIntoSubSequences(chromosome);
			String name = sequencesFile.getName();
			String baseName = FilenameUtils.getBaseName(name);
			String extension = FilenameUtils.getExtension(name);
			File tempFile = File.createTempFile(baseName + "-", "." + extension);
			FastaSplitUtils.writeSubSequences(tempFile, originalSequences);
			tempFile.renameTo(sequencesFile);
		}
		return originalSequences;
	}

}

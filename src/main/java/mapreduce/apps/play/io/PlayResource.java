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
package mapreduce.apps.play.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import edu.wustl.cse231s.download.DownloadUtils;
import edu.wustl.cse231s.lazy.Lazy;
import mapreduce.apps.play.core.PlayLine;
import mapreduce.apps.play.core.PlayRole;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public enum PlayResource {
	HAMLET("https://www.gutenberg.org/files/1524/1524-0.txt");
	private final Lazy<List<String>> input;

	private PlayResource(String spec) {
		this.input = new Lazy<>(() -> {
			try {
				File file = DownloadUtils.getDownloadedFile(new URL(spec));
				return FileUtils.readLines(file, StandardCharsets.UTF_8);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public void fetch() {
		this.input.get();
	}

	public PlayLine[] getLines() {
		List<String> textLines = input.get();
		boolean isStarted = false;
		PlayRole role = null;
		List<String> words = null;
		List<PlayLine> roleLines = new LinkedList<>();

		Pattern pattern = Pattern.compile("([A-Z ]*)\\. (.*)");
		for (String text : textLines) {
			if (!isStarted && text.contains("BARNARDO. Who")) {
				isStarted = true;
			}
			if (isStarted) {
				if (text.contains(" [_A dead march._]")) {
					break;
				}
				if (text.startsWith("ACT ") || text.startsWith("[SCENE ") || text.startsWith("Enter ")) {
					// pass
				} else {
					text = text.replaceAll("\\[_.*_\\]", "").trim();
					if (text.length() > 0) {
						Matcher matcher = pattern.matcher(text);
						if (text.matches("([A-Z ]*)\\. (.*)")) {
							matcher.find();
							if (role != null) {
								PlayLine line = new PlayLine(role, words);
								roleLines.add(line);
							}
							String[] array = text.split("[^\\w']+");
							role = new PlayRole(matcher.group(1));
							words = new LinkedList<>();
							for (int i = 1; i < array.length; ++i) {
								words.add(array[i]);
							}
						} else {
							String[] array = text.split("[^\\w']+");
							for (String s : array) {
								words.add(s);
							}
						}
					}
				}

			}
		}
		PlayLine[] result = new PlayLine[roleLines.size()];
		return roleLines.toArray(result);
	}
}

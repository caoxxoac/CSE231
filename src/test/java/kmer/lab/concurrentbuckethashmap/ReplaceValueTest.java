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
package kmer.lab.concurrentbuckethashmap;

import static edu.wustl.cse231s.v5.V5.launchApp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import edu.wustl.cse231s.junit.JUnitUtils;
import kmer.lab.rubric.KMerRubric;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 *         {@link ConcurrentBucketHashMap#put(Object, Object)}
 */
@KMerRubric(KMerRubric.Category.BUCKET_MAP)
public class ReplaceValueTest extends AbstractDictionaryTest {
	@Rule
	public TestRule timeout = JUnitUtils.createTimeoutRule();

	@Test
	public void testPutReplaceValue() {
		launchApp(() -> {
			ConcurrentBucketHashMap<String, String> mapNameToPlace = createMap();
			String bearName = "Paddington";
			mapNameToPlace.put(bearName, "Peru");
			mapNameToPlace.put(bearName, "London");
			assertNotEquals("Peru", mapNameToPlace.get(bearName));
			assertEquals("London", mapNameToPlace.get(bearName));
		});
	}
}

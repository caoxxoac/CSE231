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
package filter.studio;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import filter.studio.FilterApps;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class AllWordsContainAllVowelsTest {
	static List<String> getAllWordsContainAllVowelsList() {
		return Arrays.asList("aeronautic", "ambidextrous", "auctioneer", "augmentation",
				"authenticator", "authorize", "autobiographies", "automobile", "beautifications", "boundaries",
				"capriciousness", "cauliflower", "cautioned", "chivalrousness", "communicate", "conceptualize",
				"configurable", "consequential", "dialogue", "discourage", "documentaries", "education", "emulation",
				"encapsulation", "enunciation", "equation", "euphoria", "evacuation", "evaluation", "evolutionary",
				"excommunicate", "exhaustion", "functionalities", "graciousness", "gratuitousness", "gregarious",
				"harmoniousness", "incomputable", "inconsequential", "instantaneous", "institutionalize",
				"instrumentation", "insubordinate", "insurmountable", "intercommunicate", "intravenous", "jealousies",
				"malfunctioned", "maliciousness", "Milquetoast", "miscellaneous", "modularize", "mountaineer",
				"multidimensional", "neurological", "nonnumerical", "nonsequential", "ostentatious", "overhauling",
				"pandemonium", "permutation", "perpetuation", "persuasion", "perturbation", "pneumonia", "popularize",
				"precarious", "precaution", "preoccupation", "pseudoparallelism", "psychotherapeutic", "questionnaire",
				"recalculation", "reconfigurable", "regulation", "reputation", "reticulation", "revolutionary",
				"sacrilegious", "semiautomated", "Sequoia", "simultaneous", "speculation", "subordinate", "tautologies",
				"telecommunication", "tenacious", "unauthorized", "unavoidable", "unconstrained", "uncoordinated",
				"underestimation", "unequivocal", "unidirectional", "unintentional", "unquestionably", "unrecognizable",
				"unsophisticated", "villainousness", "vocabularies");
	}
	@Test
	public void testAllTrue() {
		List<String> original = getAllWordsContainAllVowelsList();
		List<String> input = new LinkedList<>(original);
		List<String> actual = FilterApps.filterWordsWhichContainAllVowels(input);
		assertEquals("should not mutate input", original, input);
		assertEquals(original, actual);
	}

}

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

package iterativeaveraging.warmup;

import static edu.wustl.cse231s.v5.V5.forall;
import static edu.wustl.cse231s.v5.V5.launchApp;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Phaser;

import edu.wustl.cse231s.NotYetImplementedException;

/**
 * @author __STUDENT_NAME__
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class PhaserWarmup {
	public static void warmup(List<Character> letters, List<Integer> digits)
			throws InterruptedException, ExecutionException {
		System.out.println("TODO: warmup");
	}

	public static void warmupPhased(List<Character> letters, List<Integer> digits)
			throws InterruptedException, ExecutionException {
		System.out.println("TODO: warmupPhased");
	}

	public static void main(String[] args) {
		List<Character> letters = Arrays.asList('A', 'B', 'C', 'D');
		List<Integer> digits = Arrays.asList(1, 2, 3);
		launchApp(() -> {
			warmup(letters, digits);
			System.out.println();
			warmupPhased(letters, digits);
		});
	}

}

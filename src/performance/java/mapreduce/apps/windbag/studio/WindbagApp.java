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
package mapreduce.apps.windbag.studio;

import static edu.wustl.cse231s.v5.V5.launchAppWithReturn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collector;

import edu.wustl.cse231s.util.Maps;
import mapreduce.apps.play.core.PlayLine;
import mapreduce.apps.play.core.PlayRole;
import mapreduce.apps.play.io.PlayResource;
import mapreduce.framework.core.MapReduceFramework;
import mapreduce.framework.core.Mapper;
import mapreduce.framework.lab.simple.SimpleMapReduceFramework;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class WindbagApp {
	private static <E, K, V, A, R> MapReduceFramework<E, K, V, A, R> createFramework(Mapper<E, K, V> mapper,
			Collector<V, A, R> collector) {
		return new SimpleMapReduceFramework<>(mapper, collector);
	}

	private static <E, K, V, A, R> Map<K, R> mapReduceAll(Mapper<E, K, V> mapper, Collector<V, A, R> collector,
			E[] input) {
		MapReduceFramework<E, K, V, A, R> framework = createFramework(mapper, collector);
		return launchAppWithReturn(() -> {
			return framework.mapReduceAll(input);
		});
	}

	public static void main(String[] args) {
		PlayLine[] input = PlayResource.HAMLET.getLines();
		Mapper<PlayLine, PlayRole, Boolean> mapper = new WindbagMapper((count) -> count > 100);
		Collector<Boolean, ?, Integer> countCollector = new TrueCountClassicReducer();
		Collector<Boolean, ?, Double> portionCollector = new TruePortionClassicReducer();

		System.out.println("           COUNT");
		System.out.println("===========================");
		Map<PlayRole, Integer> mapCount = mapReduceAll(mapper, countCollector, input);
		List<Entry<PlayRole, Integer>> countSortedByValue = Maps.entriesSortedBy(mapCount,
				(a, b) -> Integer.compare(b.getValue(), a.getValue()));
		countSortedByValue.forEach(entry -> {
			if (entry.getValue() > 0) {
				System.out.printf("%12s: %2d\n", entry.getKey().getName(), entry.getValue());
			}
		});

		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("          PORTION");
		System.out.println("===========================");
		Map<PlayRole, Double> mapPortion = mapReduceAll(mapper, portionCollector, input);
		List<Entry<PlayRole, Double>> countPortionByValue = Maps.entriesSortedBy(mapPortion,
				(a, b) -> Double.compare(b.getValue(), a.getValue()));
		countPortionByValue.forEach(entry -> {
			if (entry.getValue() > 0) {
				System.out.printf("%12s: %f\n", entry.getKey().getName(), entry.getValue());
			}
		});
	}
}

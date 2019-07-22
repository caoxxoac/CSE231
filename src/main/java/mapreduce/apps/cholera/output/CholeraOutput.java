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
package mapreduce.apps.cholera.output;

import static edu.wustl.cse231s.v5.V5.launchAppWithReturn;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collector;

import edu.wustl.cse231s.util.Maps;
import mapreduce.apps.cholera.core.CholeraDeath;
import mapreduce.apps.cholera.core.SohoCholeraOutbreak1854;
import mapreduce.apps.cholera.core.WaterPump;
import mapreduce.apps.cholera.studio.CholeraApp;
import mapreduce.framework.core.MapReduceFramework;
import mapreduce.framework.core.Mapper;
import mapreduce.framework.lab.simple.SimpleMapReduceFramework;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class CholeraOutput {
	public static <V extends Number, A, R extends Number> void main(String[] args) {
		Mapper<CholeraDeath, WaterPump, V> mapper = (Mapper<CholeraDeath, WaterPump, V>) CholeraApp.createMapper();
		Collector<V, A, R> collector = (Collector<V, A, R>) CholeraApp.createCollector();
		MapReduceFramework<CholeraDeath, WaterPump, V, A, R> framework = new SimpleMapReduceFramework<>(mapper,
				collector);
		Map<WaterPump, R> map = launchAppWithReturn(() -> {
			return framework.mapReduceAll(SohoCholeraOutbreak1854.getDeaths());
		});

		List<Entry<WaterPump, R>> sortedEntries = Maps.entriesSortedBy(map, (a, b) -> {
			return Double.compare(b.getValue().doubleValue(), a.getValue().doubleValue());
		});
		for (Entry<WaterPump, R> entry : sortedEntries) {
			System.out.printf("%33s: %3d\n", entry.getKey().name(), entry.getValue());
		}
	}
}

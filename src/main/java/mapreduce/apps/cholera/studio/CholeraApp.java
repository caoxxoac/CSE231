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
package mapreduce.apps.cholera.studio;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableInt;

import edu.wustl.cse231s.NotYetImplementedException;
import mapreduce.apps.cholera.core.CholeraDeath;
import mapreduce.apps.cholera.core.SohoCholeraOutbreak1854;
import mapreduce.apps.cholera.core.WaterPump;
import mapreduce.collector.intsum.studio.IntSumCollector;
import mapreduce.collector.studio.ClassicReducer;
import mapreduce.framework.core.Mapper;

/**
 * @author Xiangzhi Cao
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class CholeraApp {
	public static CholeraAppValueRepresentation getValueRepresentation() {
		return CholeraAppValueRepresentation.LOW_NUMBERS_SUSPECT;
	}

	public static double getThresholdIfApplicable() {
		final boolean IS_THRESHOLD_APPLICABLE = false;
		if (IS_THRESHOLD_APPLICABLE) {
			throw new NotYetImplementedException();
		} else {
			return Double.NaN;
		}
	}

	public static boolean isThresholdApplicable() {
		return Double.isNaN(getThresholdIfApplicable());
	}

	public static Mapper<CholeraDeath, WaterPump, Number> createMapper() {
		Mapper<CholeraDeath, WaterPump, Number> mapper = (CholeraDeath cholera, BiConsumer<WaterPump, Number> collection)->{
			for (WaterPump pump : WaterPump.values()) {
				double distance = pump.getLocation().getDistanceTo(cholera.getLocation());
				collection.accept(pump, distance);
			}
		};
		
		return mapper;
	}

	public static Collector<? extends Number, ?, ? extends Number> createCollector() {
		return new DoubleSumCollector();
	}
}

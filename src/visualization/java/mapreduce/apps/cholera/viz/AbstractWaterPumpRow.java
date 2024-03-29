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

package mapreduce.apps.cholera.viz;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import mapreduce.apps.cholera.core.WaterPump;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public abstract class AbstractWaterPumpRow {
	public static final String WATER_PUMP_PROPERTY_NAME = "waterPump";
	public static final String INSTRUCTOR_VALUE_PROPERTY_NAME = "instructorValue";
	public static final String STUDENT_VALUE_PROPERTY_NAME = "studentValue";
	public static final String MATCH_PROPERTY_NAME = "match";

	private final ObjectProperty<WaterPump> waterPumpProperty = new SimpleObjectProperty<>(this,
			WATER_PUMP_PROPERTY_NAME);
	private final BooleanProperty matchProperty = new SimpleBooleanProperty(this, MATCH_PROPERTY_NAME);

	public AbstractWaterPumpRow(WaterPump waterPump, boolean isMatch) {
		waterPumpProperty.set(waterPump);
		matchProperty.set(isMatch);
	}

	public ObjectProperty<WaterPump> waterPumpProperty() {
		return waterPumpProperty;
	}

	public BooleanProperty matchProperty() {
		return matchProperty;
	}
}

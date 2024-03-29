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
package raytrace.demo;

import static edu.wustl.cse231s.v5.V5.async;
import static edu.wustl.cse231s.v5.V5.finish;

import java.util.Deque;
import java.util.concurrent.ExecutionException;

import raytrace.core.RayTraceContext;
import raytrace.core.RayTraceTaskContext;
import raytrace.core.RayTraceUtils;
import raytrace.core.RayTracer;
import raytrace.core.Section;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class SplitFourWayRayTracer implements RayTracer {
	private void renderQuadrant(RayTraceTaskContext taskContext, int xMin, int yMin, int xMax, int yMax, int id) {
		final int SECTION_DIMENSION_SIZE = 16;
		Deque<Section> sections = RayTraceUtils.createSections(xMin, yMin, xMax, yMax, SECTION_DIMENSION_SIZE);
		for (Section section : sections) {
			section.render(taskContext, id);
		}
	}

	@Override
	public void rayTrace(RayTraceContext context) throws InterruptedException, ExecutionException {
		int xMin = 0;
		int yMin = 0;
		int xMax = context.getWidth();
		int yMax = context.getHeight();
		int xMid = (xMin + xMax) / 2;
		int yMid = (yMin + yMax) / 2;
		finish(() -> {
			async(() -> {
				renderQuadrant(context.createTaskContext(), xMin, yMin, xMid, yMid, 0);
			});
			async(() -> {
				renderQuadrant(context.createTaskContext(), xMin, yMid, xMid, yMax, 1);
			});
			async(() -> {
				renderQuadrant(context.createTaskContext(), xMid, yMin, xMax, yMid, 2);
			});
			renderQuadrant(context.createTaskContext(), xMid, yMid, xMax, yMax, 3);
		});
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}

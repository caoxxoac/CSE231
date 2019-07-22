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
package raytrace.fun;

import static edu.wustl.cse231s.v5.V5.async;
import static edu.wustl.cse231s.v5.V5.finish;

import java.util.concurrent.ExecutionException;
import java.util.function.IntPredicate;

import edu.wustl.cse231s.NotYetImplementedException;
import raytrace.core.RayTraceContext;
import raytrace.core.RayTracer;
import raytrace.core.Section;
import raytrace.core.task.TaskIdUtils;

/**
 * @author Xiangzhi Cao
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class DivideAndConquerRayTracer implements RayTracer {
	private final IntPredicate areaThreshold;

	public DivideAndConquerRayTracer(IntPredicate areaThreshold) {
		this.areaThreshold = areaThreshold;
	}

	private static void rayTraceBaseCase(RayTraceContext context, int xMin, int yMin, int xMax, int yMax) {
		Section section = new Section(xMin, yMin, xMax, yMax);
		section.render(context.createTaskContext(), TaskIdUtils.getTaskId());
	}

	private void rayTraceKernel(RayTraceContext context, int xMin, int yMin, int xMax, int yMax) {
		// divide and conquer in parallel until you reach areaThreshold
		// then invoke rayTraceBaseCase
		throw new NotYetImplementedException();
	}

	@Override
	public void rayTrace(RayTraceContext context) throws InterruptedException, ExecutionException {
		finish(() -> {
			rayTraceKernel(context, 0, 0, context.getWidth(), context.getHeight());
		});
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}

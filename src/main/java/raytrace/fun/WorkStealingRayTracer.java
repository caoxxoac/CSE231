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

import static edu.wustl.cse231s.v5.V5.forall;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.wustl.cse231s.NotYetImplementedException;
import raytrace.core.RayTraceContext;
import raytrace.core.RayTraceTaskContext;
import raytrace.core.RayTraceUtils;
import raytrace.core.RayTracer;
import raytrace.core.Section;

/**
 * @author Xiangzhi Cao
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class WorkStealingRayTracer implements RayTracer {

	private void renderMyTasksUntilEmptyThenStealWorkFromOthers(RayTraceTaskContext taskContext,
			Deque<Section> myTaskDeque, List<Deque<Section>> otherTaskDeques, int id) {
		throw new NotYetImplementedException();
	}

	@Override
	public void rayTrace(RayTraceContext context) throws InterruptedException, ExecutionException {
		int xMin = 0;
		int yMin = 0;
		int xMax = context.getWidth();
		int yMax = context.getHeight();
		int xMid = (xMin + xMax) / 2;
		int yMid = (yMin + yMax) / 2;

		final int SECTION_DIMENSION_SIZE = 16;
		List<Deque<Section>> allDeques = Arrays.asList(
				RayTraceUtils.createSections(xMin, yMin, xMid, yMid, SECTION_DIMENSION_SIZE),
				RayTraceUtils.createSections(xMin, yMid, xMid, yMax, SECTION_DIMENSION_SIZE),
				RayTraceUtils.createSections(xMid, yMin, xMax, yMid, SECTION_DIMENSION_SIZE),
				RayTraceUtils.createSections(xMid, yMid, xMax, yMax, SECTION_DIMENSION_SIZE));

		forall(0, allDeques.size(), (int id) -> {
			renderMyTasksUntilEmptyThenStealWorkFromOthers(context.createTaskContext(), myTaskDeque(allDeques, id),
					otherTaskDeques(allDeques, id), id);
		});
	}

	private static Deque<Section> myTaskDeque(List<Deque<Section>> allDeques, int id) {
		return allDeques.get(id);
	}

	private static List<Deque<Section>> otherTaskDeques(List<Deque<Section>> allDeques, int id) {
		List<Deque<Section>> result = new LinkedList<>(allDeques);
		result.remove(id);
		return result;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}

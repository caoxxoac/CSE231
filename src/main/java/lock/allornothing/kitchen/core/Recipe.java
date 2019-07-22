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

package lock.allornothing.kitchen.core;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Miles Cushing (mcushing@wustl.edu)
 */
public class Recipe {
	private final List<Appliance> requiredAppliances;

	public Recipe(List<Appliance> requiredAppliances) {
		this.requiredAppliances = requiredAppliances;
	}

	public List<Appliance> getRequiredAppliances() {
		return requiredAppliances;
	}

	public void create() {
		for (Appliance appliance : requiredAppliances) {
			ReentrantLock lock = appliance.getLock();
			if (lock.isHeldByCurrentThread()) {
				// pass
			} else {
				throw new IllegalThreadStateException();
			}
		}
	}
}

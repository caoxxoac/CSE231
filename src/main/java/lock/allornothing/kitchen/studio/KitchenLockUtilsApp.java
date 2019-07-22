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

package lock.allornothing.kitchen.studio;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import edu.wustl.cse231s.IntendedForStaticAccessOnlyError;
import edu.wustl.cse231s.NotYetImplementedException;
import lock.allornothing.kitchen.core.Appliance;
import lock.allornothing.kitchen.core.Recipe;
import lock.allornothing.util.studio.LockUtils;

/**
 * @author Xiangzhi Cao
 * @author Miles Cushing (mcushing@wustl.edu)
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class KitchenLockUtilsApp {

	private KitchenLockUtilsApp() {
		throw new IntendedForStaticAccessOnlyError();
	}

	/**
	 * Executes a given recipe. This method needs to be thread-safe by utilizing the
	 * Reentrant locks that associated with the appliances in the recipe. Since it
	 * is unknown how many locks there will be, you should use the
	 * {@link LockUtils#runWithAllLocks(List, Runnable)} method defined earlier in
	 * the studio. Finally, use the recipe's create() method to finish up.
	 * 
	 * @param recipe,
	 *            the Recipe that contains a list of appliances needed to make cook
	 *            the item
	 * @return whether or not the recipe could be executed at this time.
	 */
	public static boolean executeRecipe(Recipe recipe) {
		List<ReentrantLock> locks = new ArrayList<ReentrantLock>();
		for (Appliance appliance : recipe.getRequiredAppliances()) {
			locks.add(appliance.getLock());
		}
		boolean run = LockUtils.runWithAllLocksOrDontRunAtAll(locks, new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				recipe.create();
			}
		});
		if (run) {
			return true;
		}
			
		return false;
	}
}

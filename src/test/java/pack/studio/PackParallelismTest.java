package pack.studio;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.wustl.cse231s.junit.JUnitUtils;
import edu.wustl.cse231s.v5.bookkeep.BookkeepingUtils;
import pack.util.ValueGenerator;

@RunWith(Parameterized.class)
public class PackParallelismTest {

	private final ValueGenerator valueGenerator;
	private final int length;

	public PackParallelismTest(ValueGenerator valueGenerator, int length) {
		this.valueGenerator = valueGenerator;
		this.length = length;
	}

	@Test
	public void test() throws InterruptedException, ExecutionException {
		Integer[] arr = new Integer[this.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = this.valueGenerator.applyAsInt(i);
		}
		Predicate<Integer> predicate = i -> (i == 0);
		BookkeepingUtils.bookkeep(() -> {
			ParallelPack.pack(Integer[].class, arr, predicate);
		}, (bookkeep) -> {
			int powerOfTwoCount = 0;
			int value = 1;
			while (value < length) {
				powerOfTwoCount++;
				value *= 2;
			}
			// Checking the number of forall invoked.
			assertEquals(2 + powerOfTwoCount, bookkeep.getForasyncTotalInvocationCount());
			assertEquals(2 + powerOfTwoCount, bookkeep.getNonAccumulatorFinishInvocationCount());
			assertEquals((2 + powerOfTwoCount) * arr.length, bookkeep.getAsyncViaForasyncCount());
		});
	}

	@Parameters(name = "{0} length={1}")
	public static Collection<Object[]> getConstructorArguments() {
		return JUnitUtils.toParameterizedArguments2(ValueGenerator.values(),
				new Integer[] { 1, 2, 3, 4, 8, 31, 32, 33, 71, 231 });
	}
}

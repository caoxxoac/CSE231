package pack.studio;

import static edu.wustl.cse231s.v5.V5.launchApp;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.wustl.cse231s.junit.JUnitUtils;
import pack.util.ValueGenerator;

@RunWith(Parameterized.class)
public class PackPrimitiveCorrectnessTest {

	private final ValueGenerator valueGenerator;
	private final int length;

	public PackPrimitiveCorrectnessTest(ValueGenerator valueGenerator, int length) {
		this.valueGenerator = valueGenerator;
		this.length = length;
	}

	@Test
	public void test() throws InterruptedException, ExecutionException {
		Integer[] arr = new Integer[this.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = this.valueGenerator.applyAsInt(i);
		}
		Predicate<Integer> predicate;
		switch (this.valueGenerator) {
		case ONES_AND_ZEROS:
			predicate = (i) -> i == 0;
			break;

		case RANDOM_0_TO_1000:
			predicate = (i) -> i < 500;
			break;
		default:
			predicate = (i) -> i < 500;
			break;
		}
		int count = 0;
		for (int i : arr) {
			if (predicate.test(i))
				count++;
		}
		final int _count = count;
		Integer[] expectedArr = new Integer[count];
		int index = 0;
		for (int i : arr) {
			if (predicate.test(i)) {
				expectedArr[index++] = i;
			}
		}
		launchApp(() -> {
			Integer[] result = ParallelPack.pack(Integer[].class, arr, predicate);
			assertEquals(_count, result.length);
			assertArrayEquals(expectedArr, result);
		});
	}

	@Parameters(name = "{0} length={1}")
	public static Collection<Object[]> getConstructorArguments() {
		return JUnitUtils.toParameterizedArguments2(ValueGenerator.values(),
				new Integer[] { 1, 2, 3, 4, 8, 31, 32, 33, 71, 231 });
	}
}

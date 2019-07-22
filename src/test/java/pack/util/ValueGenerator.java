package pack.util;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntUnaryOperator;

/**
 * @author Will Zhao
 */
public enum ValueGenerator implements IntUnaryOperator {
	ONES_AND_ZEROS() {
		@Override
		public int applyAsInt(int operand) {
			return (ThreadLocalRandom.current().nextInt(1000) > 500) ? 1 : 0;
		}
	},

	RANDOM_0_TO_1000() {
		@Override
		public int applyAsInt(int operand) {
			return ThreadLocalRandom.current().nextInt(1000);
		}

	};

}

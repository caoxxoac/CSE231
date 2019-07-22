package util.lab.map;

import java.util.Collection;

import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.wustl.cse231s.junit.JUnitUtils;

@RunWith(Parameterized.class)
public abstract class AbstractMapTest {
	private final CollectionSupplier collectionSupplier;

	public AbstractMapTest(CollectionSupplier collectionSupplier) {
		this.collectionSupplier = collectionSupplier;
	}

	protected final <K, V> BucketsHashMap<K, V> createMap() {
		return new BucketsHashMap<>(collectionSupplier.getSupplier());
	}

	@Rule
	public TestRule timeout = JUnitUtils.createTimeoutRule();

	@Parameters(name = "{0}")
	public static Collection<Object[]> getConstructorArguments() {
		return JUnitUtils.toParameterizedArguments(CollectionSupplier.values());
	}
}

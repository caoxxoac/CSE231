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
package kmer.lab.concurrentbuckethashmap;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiFunction;

import edu.wustl.cse231s.NotYetImplementedException;
import edu.wustl.cse231s.util.KeyMutableValuePair;
import javax.annotation.concurrent.ThreadSafe;

/**
 * @author Xiangzhi Cao
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
@ThreadSafe
public class ConcurrentBucketHashMap<K, V> implements ConcurrentMap<K, V> {
	private final List<Entry<K, V>>[] buckets;
	private final ReadWriteLock[] locks;

	@SuppressWarnings("unchecked")
	public ConcurrentBucketHashMap(int bucketCount) {
		this.buckets = new List[bucketCount];
		this.locks = new ReadWriteLock[bucketCount];

		for (int i=0; i<bucketCount; i++) {
			this.buckets[i] = new LinkedList<Entry<K, V>>();
			this.locks[i] = new ReentrantReadWriteLock();
		}
	}

	private int getIndex(Object key) {
		int hash = key.hashCode();
		int length = this.buckets.length;
		int index = Math.floorMod(hash, length);
		return index;
	}

	private List<Entry<K, V>> getBucket(Object key) {
		int index = this.getIndex(key);
		return this.buckets[index];
	}

	private ReadWriteLock getLock(Object key) {
		int index = this.getIndex(key);
		return this.locks[index];
	}

	private static <K, V> Entry<K, V> getEntry(List<Entry<K, V>> bucket, Object key) {
		for (Entry<K, V> entry : bucket) {
			if (entry.getKey().equals(key)) {
				return entry;
			}
		}
		return null;
	}

	@Override
	public V get(Object key) {
		ReadWriteLock lock = this.getLock(key);
		try {
			lock.readLock().lock();
			List<Entry<K, V>> bucket = this.getBucket(key);
			Entry<K, V> entry = getEntry(bucket, key);
			if (entry == null) {
				return null;
			}
			return entry.getValue();
		}
		finally {
			lock.readLock().unlock();
		}

	}

	@Override
	public V put(K key, V value) {
		ReadWriteLock lock = this.getLock(key);
		try {
			lock.writeLock().lock();
			List<Entry<K, V>> bucket = this.getBucket(key);
			Entry<K, V> entry = getEntry(bucket, key);
			if (entry == null) {
				entry = new KeyMutableValuePair<K, V>(key, value);
				bucket.add(entry);
				return value;
			}
			else {
				V previousValue = entry.getValue();
				entry.setValue(value);
				return previousValue;
			}
		}
		finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		ReadWriteLock lock = this.getLock(key);
		try {
			lock.writeLock().lock();
			List<Entry<K, V>> bucket = this.getBucket(key);
			Entry<K, V> entry = getEntry(bucket, key);
			V value;
			if (entry == null) {
				value = remappingFunction.apply(key, null);

			}
			else {
				value = remappingFunction.apply(key, entry.getValue());
			}
			put(key, value);
			return value;
		}
		finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public int size() {
		throw new RuntimeException("not required");
	}

	@Override
	public boolean isEmpty() {
		throw new RuntimeException("not required");
	}

	@Override
	public boolean containsKey(Object key) {
		throw new RuntimeException("not required");
	}

	@Override
	public boolean containsValue(Object value) {
		throw new RuntimeException("not required");
	}

	@Override
	public V putIfAbsent(K key, V value) {
		throw new RuntimeException("not required");
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		throw new RuntimeException("not required");
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		throw new RuntimeException("not required");
	}

	@Override
	public V replace(K key, V value) {
		throw new RuntimeException("not required");
	}

	@Override
	public void clear() {
		throw new RuntimeException("not required");
	}

	@Override
	public boolean remove(Object key, Object value) {
		throw new RuntimeException("not required");
	}

	@Override
	public V remove(Object key) {
		throw new RuntimeException("not required");
	}

	@Override
	public Set<K> keySet() {
		throw new RuntimeException("not required");
	}

	@Override
	public Collection<V> values() {
		throw new RuntimeException("not required");
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		throw new RuntimeException("not required");
	}
}

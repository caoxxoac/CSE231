/*******************************************************************************
 * Copyright (C) 2016-2017 Dennis Cosgrove, Ben Choi
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
package util.lab.map;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.collections4.iterators.LazyIteratorChain;

import edu.wustl.cse231s.NotYetImplementedException;
import edu.wustl.cse231s.util.KeyMutableValuePair;

/**
 * @author __STUDENT_NAME__
 * @author Ben Choi (benjaminchoi@wustl.edu)
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
@NotThreadSafe
public class BucketsHashMap<K, V> extends AbstractMap<K, V> {
	private final Collection<Entry<K, V>>[] buckets;
	private final Supplier<Collection<Entry<K, V>>> collectionSupplier;

	@SuppressWarnings("unchecked")
	public BucketsHashMap(int capacity, Supplier<Collection<Entry<K, V>>> collectionSupplier) {
		this.buckets = new Collection[capacity];
		this.collectionSupplier = collectionSupplier;
		for (int i=0; i<capacity; i++) {
			this.buckets[i] = this.collectionSupplier.get();
		}
	}

	public BucketsHashMap(Supplier<Collection<Entry<K, V>>> collectionSupplier) {
		this(DEFAULT_BUCKET_COUNT, collectionSupplier);
	}

	/**
	 * @param key
	 * @return index of the bucket for the specified key
	 */
	private int getBucketIndex(Object key) {
		if (key.hashCode() < 0) {
			return this.buckets.length + key.hashCode() % this.buckets.length;
		}
		return key.hashCode() % this.buckets.length;
	}

	/**
	 * @param key
	 * @return the bucket for the specified key
	 */
	private Collection<Entry<K, V>> getBucketFor(Object key) {
		int hashCode = this.getBucketIndex(key);
		return this.buckets[hashCode];
	}

	@Override
	public int size() {
		int count = 0;
		for (int i=0; i<this.buckets.length; i++) {
			if (this.buckets[i] != null) {
				count += this.buckets[i].size();
			}
		}
		return count;
	}

	@Override
	public V put(K key, V value) {
		if (value == null || key == null) {
			return null;
		}
	
		V prevsValue = null;
		Entry<K, V> map = new KeyMutableValuePair<K, V>(key, value);

		for (Entry<K, V> entry : this.getBucketFor(key)) {
			if (entry.getKey().equals(key)) {
				prevsValue = entry.getValue();
				entry.setValue(value);
				return prevsValue;
			}
		}
		this.getBucketFor(key).add(map);
		return null;
	}

	@Override
	public V remove(Object key) {
		if (key == null) {
			return null;
		}
		for (Entry<K, V> entry : this.entrySet()) {
			if (entry.getKey().equals(key)) {
				// remove
				V value = entry.getValue();
				this.getBucketFor(key).remove(entry);
				return value;
			}
		}
		return null;
	}

	@Override
	public V get(Object key) {
		if (key == null) {
			return null;
		} 
		// search through the collection, 
		// and try to find the object has the same key as provided
		for (Entry<K, V> entry : this.getBucketFor(key)) {
			if (entry.getKey().equals(key)) {
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return new AbstractSet<Entry<K, V>>() {
			@Override
			public Iterator<Entry<K, V>> iterator() {
				return new LazyIteratorChain<Map.Entry<K, V>>() {
					@Override
					protected Iterator<? extends java.util.Map.Entry<K, V>> nextIterator(int count) {
						int index = count - 1;
						return index < BucketsHashMap.this.buckets.length
								? BucketsHashMap.this.buckets[index].iterator()
								: null;
					}
				};
			}

			@Override
			public int size() {
				return BucketsHashMap.this.size();
			}
		};
	}

	private static final int DEFAULT_BUCKET_COUNT = 1024;
}

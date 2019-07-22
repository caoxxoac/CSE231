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

package util.lab.collection;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public enum NodesCreator {
	DIRECT_CREATION() {
		@Override
		public <E> DoublyLinkedNode<E> createNodes(Iterable<E> values) {
			DoublyLinkedNode<E> sentinel = DoublyLinkedNodeUtils.createSentinel();
			DoublyLinkedNode<E> prev = sentinel;
			for (E value : values) {
				DoublyLinkedNode<E> node = new DoublyLinkedNode<>(value);
				DoublyLinkedNodeUtils.hookUpNodes(prev, node);
				prev = node;
			}
			return sentinel;
		}
	},
	VIA_COLLECTION_ADD_AND_REFLECTION() {
		@Override
		public <E> DoublyLinkedNode<E> createNodes(Iterable<E> values)
				throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
			LinkedCollection<E> collection = new LinkedCollection<>();
			for (E value : values) {
				collection.add(value);
			}
			return AccessCollectionFieldUtils.getHeadFieldValue(collection);
		}
	};
	public abstract <E> DoublyLinkedNode<E> createNodes(Iterable<E> values) throws Exception;
}

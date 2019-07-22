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
package util.lab.collection;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * @author Ben Choi (benjaminchoi@wustl.edu)
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
@NotThreadSafe
/* package-private */class DoublyLinkedNode<E> {

	private final E value;
	private DoublyLinkedNode<E> prev;
	private DoublyLinkedNode<E> next;

	public DoublyLinkedNode(E value) {
		this.value = value;
	}

	/**
	 * Gets the previous node the given node is pointing to
	 * 
	 * @return previous node in the list
	 */
	public DoublyLinkedNode<E> getPrev() {
		return prev;
	}

	/**
	 * Sets the value of the previous node of the given node
	 * 
	 * @param prev
	 */
	public void setPrev(DoublyLinkedNode<E> prev) {
		this.prev = prev;
	}

	/**
	 * Gets the next node the given node is pointing to
	 * 
	 * @return next node in the list
	 */
	public DoublyLinkedNode<E> getNext() {
		return next;
	}

	/**
	 * Sets the value of the next node of the given node
	 * 
	 * @param next
	 */
	public void setNext(DoublyLinkedNode<E> next) {
		this.next = next;
	}

	/**
	 * Gets the value of the given node
	 * 
	 * @return the value associated with the node
	 */
	public E getValue() {
		return value;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("[@");
		sb.append(System.identityHashCode(this));
		sb.append("; value=");
		sb.append(value);
		sb.append("; prev=");
		sb.append(prev != null ? "@" + System.identityHashCode(prev) : "null");
		sb.append("; next=");
		sb.append(next != null ? "@" + System.identityHashCode(next) : "null");
		sb.append("]");
		return sb.toString();
	}
}

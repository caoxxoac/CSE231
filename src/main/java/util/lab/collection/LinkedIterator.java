/*******************************************************************************
 * Copyright (C) 2016-2018 Dennis Cosgrove, Ben Choi
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import javax.annotation.concurrent.NotThreadSafe;

import edu.wustl.cse231s.NotYetImplementedException;

/**
 * @author __STUDENT_NAME__
 * @author Ben Choi (benjaminchoi@wustl.edu)
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
@NotThreadSafe
/* package-private */class LinkedIterator<E> implements Iterator<E> {
	private DoublyLinkedNode<E> head;
	private Consumer<DoublyLinkedNode<E>> tailUpdater;
	private Runnable sizeDecrementer;
	private DoublyLinkedNode<E> node;

	public LinkedIterator(DoublyLinkedNode<E> head, Consumer<DoublyLinkedNode<E>> tailUpdater, Runnable sizeDecrementer) {
		this.head = head;
		this.node = this.head;
		this.tailUpdater = tailUpdater;
		this.sizeDecrementer = sizeDecrementer;
	}

	@Override
	public boolean hasNext() {
		if (this.node == null){
			return false;
		}
		return this.node.getNext() != null;
	}

	@Override
	public E next() {
		if (this.hasNext()) {
			this.node = this.node.getNext();
			//			this.tailUpdater.accept(this.node);
			//			if (this.head == null) {
			//				this.head = this.node;
			//			}
			return this.node.getValue();
		}
		throw new NoSuchElementException();
	}

	@Override
	public void remove() {
		DoublyLinkedNode<E> nextNode = null;
		DoublyLinkedNode<E> prevNode = null;
		if (this.node.getValue() == null || this.node == this.head) {
			throw new IllegalStateException();
		}
		if (this.node.getPrev() == null) {
			this.node = this.head;
			this.head.setNext(null);
			this.tailUpdater.accept(this.head);
		}
		else {
			prevNode = this.node.getPrev();
			if (this.node.getNext() != null) {
				nextNode = this.node.getNext();

				prevNode.setNext(nextNode);
				nextNode.setPrev(prevNode);
			}
			else {
				prevNode.setNext(null);
			}
			
			if (prevNode.getNext() == null) {
				this.tailUpdater.accept(prevNode);
			}
			
			this.node = new DoublyLinkedNode<E>(null);
			this.node.setNext(nextNode);
			this.node.setPrev(prevNode);
		}
		this.sizeDecrementer.run();
	}
}

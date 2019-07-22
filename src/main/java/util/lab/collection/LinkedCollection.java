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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;

import javax.annotation.concurrent.NotThreadSafe;

import edu.wustl.cse231s.NotYetImplementedException;

/**
 * @author __STUDENT_NAME__
 * @author Ben Choi (benjaminchoi@wustl.edu)
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
@NotThreadSafe
public class LinkedCollection<E> extends AbstractCollection<E> {
	private int size;
	private final DoublyLinkedNode<E> head;
	private DoublyLinkedNode<E> tail;
	
	public LinkedCollection() {
		this.size = 0;
		this.head = new DoublyLinkedNode<E>(null);
		this.tail = this.head;
	}

	@Override
	public Iterator<E> iterator() {
		Consumer<DoublyLinkedNode<E>> tailUpdater = (t) -> this.tail = t;
		Runnable decrement = () -> {
			this.size --;
		};
		
//		this.tail = this.head;
		
//		while (this.tail.getNext() != null) {
//			if (this.tail.getValue() == this.tail.getNext().getValue()) {
//				if (this.tail.getNext().getNext() != null) {
//					this.tail.setNext(this.tail.getNext().getNext());
//				}
//				else {
//					this.tail.setNext(null);
//				}
//			}
//			else{
//				this.tail = this.tail.getNext();
//			}
//		}
		LinkedIterator<E> iter = new LinkedIterator<E>(this.head, tailUpdater, decrement);
		
		return iter;
	}

	@Override
	public int size() {
		return this.size;
	}

	/**
	 * Adds the specified item to the Collection if and only if it is not null.
	 * 
	 * @return true is the collection is changed as a result of the call
	 */
	@Override
	public boolean add(E item) {
		if (item == null) {
			return false;
		}
		if (this.tail.getValue() == null) {
			this.head.setNext(new DoublyLinkedNode<E>(item));
			this.tail = this.head.getNext();
			
			this.tail.setPrev(this.head);
			
			this.size ++;
			return true;
		}
		
		DoublyLinkedNode<E> tempTail = this.tail;
		
		this.tail.setNext(new DoublyLinkedNode<E>(item));
		this.tail = this.tail.getNext();
		
		this.tail.setPrev(tempTail);
		
		this.size ++;
		return true;
	}
}

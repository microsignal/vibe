package io.microvibe.util.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayQueue<E> implements Queue<E>, Stack<E> {
	//private int capacity = 16;
	private E[] data;
	private int size = 0;
	//private int begin = 0;

	public ArrayQueue() {
		this(16);
	}
	public ArrayQueue(int capacity) {
		data = (E[]) new Object[capacity];
	}

	public int capacity() {
		return data.length;
	}

	private void shift(int origIdx, int destIdx, int length) {
		if (origIdx > destIdx) {
			for (int i = 0; i < length; i++) {
				data[destIdx + i] = data[origIdx + i];
			}
		} else {
			for (int i = length - 1; i >= 0; i--) {
				data[destIdx + i] = data[origIdx + i];
			}
		}
	}
	private void extend() {
		if (size < data.length) {
			return;
		}
		E[] ndata = (E[]) new Object[data.length * 2];
		System.arraycopy(data, 0, ndata, 0, data.length);
		data = ndata;
	}

	@Override
	public void pushFirst(E e) {
		extend();
		shift(0, 1, size);
		data[0] = e;
		size++;
	}

	@Override
	public void pushLast(E e) {
		extend();
		data[size] = e;
		size++;
	}

	@Override
	public E pollFirst() {
		if (size == 0)
			return null;
		E e = data[0];
		size--;
		shift(1, 0, size);
		return e;
	}

	@Override
	public E pollLast() {
		if (size == 0)
			return null;
		E e = data[data.length - 1];
		size--;
		return e;
	}

	@Override
	public E peekFirst() {
		if (size == 0)
			return null;
		return data[0];
	}

	@Override
	public E peekLast() {
		if (size == 0)
			return null;
		return data[data.length - 1];
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			int curr = -1;;
			public boolean hasNext() {
				return curr + 1 <= size - 1;
			}

			public E next() {
				curr++;
				if (curr >= size)
					throw new NoSuchElementException();
				return data[curr];
			}

			public void remove() {
				if (curr < 0 || curr >= size)
					throw new NoSuchElementException();
				shift(curr + 1, curr, size - curr - 1);
				size--;
				curr--;
			}
		};
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (size > 0) {
			sb.append(data[0]);
			for (int i = 1; i < size; i++) {
				sb.append(", ").append(data[i]);
			}
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public void push(E e) {
		pushLast(e);
	}

	@Override
	public E poll() {
		return pollLast();
	}

	@Override
	public E peek() {
		return peekLast();
	}

}

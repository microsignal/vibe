package io.microvibe.util.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedQueue<E> implements Queue<E>, Stack<E> {
	static class Node<E> {
		E data;
		Node<E> prev;
		Node<E> next;
	}
	private int size = 0;
	private Node<E> head = new Node<E>();
	private Node<E> tail = new Node<E>();
	{
		head.next = tail;
		head.prev = head;
		tail.prev = head;
		tail.next = tail;
	}

	@Override
	public void pushFirst(E e) {
		Node<E> node = new Node<E>();
		node.data = e;
		node.next = head.next;
		node.prev = head;
		head.next.prev = node;
		head.next = node;
		size++;
	}

	@Override
	public void pushLast(E e) {
		Node<E> node = new Node<E>();
		node.data = e;
		node.prev = tail.prev;
		node.next = tail;
		tail.prev.next = node;
		tail.prev = node;
		size++;
	}

	@Override
	public E pollFirst() {
		if (size == 0)
			return null;
		E e = head.next.data;
		head.next.next.prev = head;
		head.next = head.next.next;
		size--;
		return e;
	}

	@Override
	public E pollLast() {
		if (size == 0)
			return null;
		E e = tail.prev.data;
		tail.prev.prev.next = tail;
		tail.prev = tail.prev.prev;
		size--;
		return e;
	}

	@Override
	public E peekFirst() {
		if (size == 0)
			return null;
		return head.next.data;
	}

	@Override
	public E peekLast() {
		if (size == 0)
			return null;
		return tail.prev.data;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			Node<E> curr = head;
			public boolean hasNext() {
				return curr.next != tail;
			}

			public E next() {
				curr = curr.next;
				if (curr == tail)
					throw new NoSuchElementException();
				return curr.data;
			}

			public void remove() {
				if (curr == head || curr == tail)
					throw new NoSuchElementException();
				curr.prev.next = curr.next;
				curr.next.prev = curr.prev;
			}
		};
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (size > 0) {
			Node<E> node = head.next;
			sb.append(node.data);
			while ((node = node.next) != tail) {
				sb.append(", ").append(node.data);
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

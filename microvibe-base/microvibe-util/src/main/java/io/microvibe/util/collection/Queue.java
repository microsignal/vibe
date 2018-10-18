package io.microvibe.util.collection;

public interface Queue<E> extends Iterable<E> {

	void pushFirst(E e);

	void pushLast(E e);

	E pollFirst();

	E pollLast();

	E peekFirst();

	E peekLast();

	int size();

}

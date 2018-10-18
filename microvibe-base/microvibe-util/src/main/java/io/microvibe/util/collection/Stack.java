package io.microvibe.util.collection;

public interface Stack<E> extends Iterable<E> {

	void push(E e);

	E poll();

	E peek();

	int size();

}

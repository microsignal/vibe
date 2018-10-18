package io.microvibe.util.collection;

import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LinkedBlockingQueue<E> extends LinkedQueue<E> implements BlockingQueue<E> {

	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private Lock readLock = readWriteLock.readLock();
	private Lock writeLock = readWriteLock.writeLock();

	@Override
	public void pushFirst(E e) {
		writeLock.lock();
		try {
			super.pushFirst(e);
		} finally {
			writeLock.unlock();
		}
	}
	@Override
	public void pushLast(E e) {
		writeLock.lock();
		try {
			super.pushLast(e);
		} finally {
			writeLock.unlock();
		}
	}
	@Override
	public E pollFirst() {
		writeLock.lock();
		try {
			return super.pollFirst();
		} finally {
			writeLock.unlock();
		}
	}
	@Override
	public E pollLast() {
		writeLock.lock();
		try {
			return super.pollLast();
		} finally {
			writeLock.unlock();
		}
	}
	@Override
	public E peekFirst() {
		readLock.lock();
		try {
			return super.peekFirst();
		} finally {
			readLock.unlock();
		}
	}
	@Override
	public E peekLast() {
		readLock.lock();
		try {
			return super.peekLast();
		} finally {
			readLock.unlock();
		}
	}
	@Override
	public int size() {
		readLock.lock();
		try {
			return super.size();
		} finally {
			readLock.unlock();
		}
	}
	@Override
	public Iterator<E> iterator() {
		return super.iterator();
	}

}

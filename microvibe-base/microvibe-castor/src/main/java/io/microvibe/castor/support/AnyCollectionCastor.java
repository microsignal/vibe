package io.microvibe.castor.support;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingDeque;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class AnyCollectionCastor<T extends Collection> extends AbstractCastor<T> {

	public AnyCollectionCastor(Class<T> type) {
		super(type);
	}

	@Override
	public final T cast(Object orig) {
		T t = null;
		if (orig != null) {
			if (type.isAssignableFrom(orig.getClass())) {
				t = (T) orig;
			} else {
				t = createCollection();
				if (t != null) {
					if (orig.getClass().isArray()) {
						final int length = Array.getLength(orig);
						for (int i = 0; i < length; i++) {
							t.add(Array.get(orig, i));
						}
					} else if (orig instanceof Collection) {
						for (final Object o : ((Collection) orig)) {
							t.add(o);
						}
					} else {
						t.add(orig);
					}
				}
			}
		}
		return t;
	}

	protected T createCollection() {
		if ((type.getModifiers() & Modifier.ABSTRACT) == 0) {
			try {
				T c = type.newInstance();
				return c;
			} catch (InstantiationException | IllegalAccessException e) {
				return null;
			}
		} else if (type.isAssignableFrom(ArrayList.class)) {
			return (T) new ArrayList();
		} else if (type.isAssignableFrom(LinkedHashSet.class)) {
			return (T) new LinkedHashSet();
		} else if (type.isAssignableFrom(HashSet.class)) {
			return (T) new HashSet();
		} else if (type.isAssignableFrom(TreeSet.class)) {
			return (T) new TreeSet();
		} else if (type.isAssignableFrom(ArrayDeque.class)) {
			return (T) new ArrayDeque();
		} else if (type.isAssignableFrom(LinkedBlockingDeque.class)) {
			return (T) new LinkedBlockingDeque();
		} else if (type.isAssignableFrom(LinkedList.class)) {
			return (T) new LinkedList();
		} else if (type.isAssignableFrom(Stack.class)) {
			return (T) new Stack();
		} else {
			return null;
		}
	}

}

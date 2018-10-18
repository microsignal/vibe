package io.microvibe.booster.commons.utils.property;

import java.util.ArrayDeque;
import java.util.Deque;

abstract class AbstractBuilder<T> implements PropertyBuilder<T> {
	protected Deque<Seriation> seriations = new ArrayDeque<>();
	protected T dest;
	protected Object lastOrig;
	protected boolean ignoredNull;

	protected class Seriation {
		Object orig;
		String origProperty;
		String destProperty;
		Object propertyValue;
		boolean ignoredNull = false;
	}

	private Seriation newSeriation(String origProperty, String destProperty, Object value) {
		Seriation seriation = new Seriation();
		if (origProperty != null) {// 需要从orig对象取值
			seriation.orig = lastOrig;
			seriation.origProperty = origProperty;
		}
		seriation.destProperty = destProperty;
		seriation.propertyValue = value;
		seriation.ignoredNull = ignoredNull;
		return seriation;
	}

	public void to(T dest) {
		this.dest = dest;
	}

	public PropertyBuilder<T> from(Object orig) {
		lastOrig = orig;
		return this;
	}

	@Override
	public PropertyBuilder<T> ignoreNull(boolean ignored) {
		ignoredNull = ignored;
		return this;
	}

	@Override
	public PropertyBuilder<T> set(String destProperty, Object value) {
		Seriation seriation = newSeriation(null, destProperty, value);
		seriations.add(seriation);
		return this;
	}

	public PropertyBuilder<T> map(String origProperty, String destProperty) {
		if (lastOrig != null) {
			Seriation seriation = newSeriation(origProperty, destProperty, null);
			seriations.add(seriation);
		}
		return this;
	}

	public PropertyBuilder<T> exec() {
		Seriation seriation = null;
		while ((seriation = seriations.poll()) != null) {
			exec(seriation);
		}
		return this;
	}

	protected abstract void exec(Seriation seriation);

	@Override
	public T done() {
		exec();
		return dest;
	}
}

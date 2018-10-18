package io.microvibe.booster.commons.utils.property;

final class StdBuilder<T> extends AbstractBuilder<T> implements PropertyBuilder<T> {
	private T dest;

	public StdBuilder(T dest) {
		this.dest = dest;
	}

	public StdBuilder(Class<T> clazz) {
		try {
			this.dest = clazz.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public StdBuilder(Object orig, T dest) {
		this(dest);
		from(orig);
	}

	public void exec(Seriation seriation) {
		Object orig = seriation.orig;
		if (orig != null) {
			Object val = PropertyUtil.getPathProperty(orig, seriation.origProperty);
			if (val != null || !seriation.ignoredNull) {
				PropertyUtil.setPathProperty(dest, seriation.destProperty, val);
			}
		} else {
			if (seriation.propertyValue != null || !seriation.ignoredNull) {
				PropertyUtil.setPathProperty(dest, seriation.destProperty, seriation.propertyValue);
			}
		}
	}

	public T done() {
		exec();
		return dest;
	}
}

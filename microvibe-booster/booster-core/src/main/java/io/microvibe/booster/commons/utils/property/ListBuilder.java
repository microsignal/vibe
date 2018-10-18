package io.microvibe.booster.commons.utils.property;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

final class ListBuilder extends AbstractBuilder<List<Object>> implements PropertyBuilder<List<Object>> {
	private List<Object> list;
	private Class<?> clazz;

	public ListBuilder(List<Object> list, Class<?> clazz) {
		init(list, clazz);
	}

	public ListBuilder(List<Object> list, Class<?> clazz, int size) {
		init(list, clazz, size);
	}

	public void init(List<Object> list, Class<?> clazz) {
		init(list, clazz, -1);
	}

	public void init(List<Object> list, Class<?> clazz, int size) {
		if (list == null) {
			this.list = new ArrayList<Object>();
		} else {
			this.list = list;
		}
		if (clazz == null) {
			this.clazz = LinkedHashMap.class;
		} else {
			this.clazz = clazz;
		}
		for (int i = this.list.size(); i < size; i++) {
			this.list.add(newOne());
		}
	}

	private Object newOne() {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	private Object getIndexObj(int i) {
		Object one;
		if (list.size() <= i) {
			one = newOne();
			list.add(one);
		} else {
			one = list.get(i);
		}
		return one;
	}

	@SuppressWarnings({"rawtypes"})
	@Override
	public void exec(Seriation seriation) {
		Object orig = seriation.orig;
		if (orig != null) {
			if (orig instanceof Collection) {
				int i = 0;
				for (Object o : (Collection) orig) {
					Object val = PropertyUtil.getPathProperty(o, seriation.origProperty);
					if (val != null || !seriation.ignoredNull) {
						Object one = getIndexObj(i);
						PropertyUtil.setPathProperty(one, seriation.destProperty, val);
					}
					i++;
				}
			} else if (orig.getClass().isArray()) {
				int len = Array.getLength(orig);
				for (int i = 0; i < len; i++) {
					Object o = Array.get(orig, i);
					Object val = PropertyUtil.getPathProperty(o, seriation.origProperty);
					if (val != null || !seriation.ignoredNull) {
						Object one = getIndexObj(i);
						PropertyUtil.setPathProperty(one, seriation.destProperty, val);
					}
				}
			} else {
				for (Object one : list) {
					Object val = PropertyUtil.getPathProperty(orig, seriation.origProperty);
					if (val != null || !seriation.ignoredNull) {
						PropertyUtil.setPathProperty(one, seriation.destProperty, val);
					}
				}
			}
		} else {
			for (Object one : list) {
				if (seriation.propertyValue != null || !seriation.ignoredNull) {
					PropertyUtil.setPathProperty(one, seriation.destProperty, seriation.propertyValue);
				}
			}
		}
	}

	@Override
	public List<Object> done() {
		exec();
		return list;
	}

}

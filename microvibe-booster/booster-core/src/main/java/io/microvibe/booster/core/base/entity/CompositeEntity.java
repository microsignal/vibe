package io.microvibe.booster.core.base.entity;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class CompositeEntity extends LinkedHashMap<String, Object> implements Serializable {
	private static final long serialVersionUID = 1L;

	public CompositeEntity(Object... objs) {
		if (objs.length > 0) {
			// detect format: key-value
			boolean isKvStyle = false;
			if ((objs.length & 1) == 0 && objs[0] instanceof String) {
				isKvStyle = true;
				Object[][] kvs = new Object[objs.length >>> 1][];
				for (int i = 0; i < objs.length; i += 2) {
					if (objs[i] instanceof String) {
						kvs[i >>> 1] = new Object[]{(String) objs[i], objs[i + 1]};
					} else {
						isKvStyle = false;
						break;
					}
				}
				if (isKvStyle) {
					for (Object[] kv : kvs) {
						this.put((String) kv[0], kv[1]);
					}
				}
			}
			if (!isKvStyle) {
				for (Object obj : objs) {
					String name = obj.getClass().getSimpleName();
					if (name.length() == 1) {
						name = name.toLowerCase();
					} else if (name.length() > 1) {
						name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
					}
					this.put(name, obj);
				}
			}
		}
	}

}

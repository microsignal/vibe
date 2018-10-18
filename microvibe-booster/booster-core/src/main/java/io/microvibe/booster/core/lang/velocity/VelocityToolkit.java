package io.microvibe.booster.core.lang.velocity;

import com.alibaba.fastjson.JSON;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VelocityToolkit {

	private static VelocityToolkit instance = new VelocityToolkit();

	public static VelocityToolkit instance() {
		return instance;
	}

	public Object coalesce(Object... args) {
		for (Object arg : args) {
			if (arg != null && !"".equals(arg)) {
				return arg;
			}
		}
		return null;
	}

	public String toJson(Object obj) {
		return JSON.toJSONString(obj);
	}
}

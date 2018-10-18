package io.microvibe.booster.commons.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mww on 2017/11/10.
 */
public class EnumUtils {

	public static <T extends Enum<T>> T valueOf(Class<T> enumType, Object value) {
		Method[] methods = enumType.getDeclaredMethods();
		for (Method method : methods) {
			if ("getValue".equals(method.getName())) {
				return valueOf(enumType, value, "getValue");
			}
		}
		return valueOf(enumType, value, "ordinal");
	}

	public static <T extends Enum<T>> List<Map> toMapList(Class<T> enumType) {
		T[] enumConstants = values(enumType);
		List<Map> mapList = new ArrayList<>();
		Method[] methods = enumType.getDeclaredMethods();
		for (T constant : enumConstants) {
			HashMap<String, Object> mapEnum = new HashMap<>();
			HashMap<String, Object> mapProperty = new HashMap<>();
			mapProperty.put("name", constant.name());
			for (Method method : methods) {
				if (method.getName().startsWith("get") && method.getParameterCount() == 0) {
					Object o;
					try {
						o = method.invoke(constant);
						mapProperty.put(method.getName().replace("get", "").toLowerCase(), o);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			mapEnum.put(constant.name(), mapProperty);
			mapList.add(mapEnum);
		}
		return mapList;
	}

	private static <T extends Enum<T>> T valueOf(Class<T> enumType, Object value, String methodName) {
		if (value == null) {
			return null;
		}
		T[] enumConstants = values(enumType);
		for (T constant : enumConstants) {
			try {
				Method getValue = enumType.getMethod(methodName);
				if (value.equals(getValue.invoke(constant))) {
					return constant;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static <T extends Enum<T>> T[] values(Class<T> enumType) {
		try {
			Method values = enumType.getMethod("values");
			return (T[]) values.invoke(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private EnumUtils() {
	}

}

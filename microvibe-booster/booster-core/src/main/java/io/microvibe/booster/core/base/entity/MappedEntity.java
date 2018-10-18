package io.microvibe.booster.core.base.entity;

import com.alibaba.fastjson.JSONObject;
import io.microvibe.booster.core.base.utils.NameCastor;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class MappedEntity extends JSONObject {

	public static <E extends MappedEntity> E newInstance(Class<E> entityClass) {
		return (E) Enhancer.create(entityClass, new MethodInterceptor() {
			@Override
			public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
				Object rs = methodProxy.invokeSuper(o, args);

				String name = method.getName();
				Class<?> returnType = method.getReturnType();
				if (args.length == 0 && !void.class.equals(returnType)) {
					if (rs == null) {
						if (name.length() > 3 && name.startsWith("get")) {
							String property = NameCastor.upperCamelToLowerCamel(name.substring(3));
							rs = ((JSONObject) o).getObject(property, returnType);
						} else if (name.length() > 2 && name.startsWith("is")) {
							String property = NameCastor.upperCamelToLowerCamel(name.substring(2));
							rs = ((JSONObject) o).getObject(property, returnType);
						}
					}
				} else if (name.length() > 3 && name.startsWith("set") && args.length == 1) {
					String property = NameCastor.upperCamelToLowerCamel(name.substring(3));
					((JSONObject) o).put(property, args[0]);
				}
				return rs;
			}
		});
	}

	@Override
	public Object get(Object key) {
		try {
			PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(this.getClass(), (String) key);
			Object rs = propertyDescriptor.getReadMethod().invoke(this);
			if (rs != null) {
				return rs;
			}
		} catch (Exception e) {
		}
		return super.get(key);
	}

	@Override
	public Object put(String key, Object value) {
		return super.put(key, value);
	}

//	public static class Wrapper extends MappedEntity {
//		@Delegate
//		MappedEntity delegate;
//
//		public Wrapper() {
//			delegate = newInstance(MappedEntity.class);
//		}
//	}
}

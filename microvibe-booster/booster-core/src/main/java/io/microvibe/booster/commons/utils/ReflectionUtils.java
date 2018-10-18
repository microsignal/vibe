package io.microvibe.booster.commons.utils;


import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.*;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ReflectionUtils extends org.springframework.util.ReflectionUtils {

	/**
	 * 得到指定类型的指定位置的泛型实参
	 *
	 * @param baseClass 基类
	 * @param objClass  目标类
	 * @param index     位置
	 * @return
	 */
	@SuppressWarnings("ALL")
	public static Class findParameterizedType(Class baseClass, Object obj, int index) {
		return findParameterizedType(baseClass, obj.getClass(), index);
	}

	/**
	 * 得到指定类型的指定位置的泛型实参
	 *
	 * @param baseClass 基类
	 * @param objClass  目标类
	 * @param index     位置
	 * @return
	 */
	@SuppressWarnings("ALL")
	public static Class findParameterizedType(Class baseClass, Class objClass, int index) {
		if (baseClass == objClass || !baseClass.isAssignableFrom(objClass)) {
			return null;
		}
		TypeVariable<? extends Class>[] typeParameters = baseClass.getTypeParameters();
		if (typeParameters.length <= index) {
			return null;
		}

		Deque<ParameterizedType> q = new ArrayDeque<>();

		// region search
		Class that = objClass;
		search:
		while (true) {
			//superclass
			Type genericSuperclass = that.getGenericSuperclass();
			if (genericSuperclass != null) {
				if (baseClass == genericSuperclass) {
					break search;
				}
				if (genericSuperclass instanceof ParameterizedType) {
					Type rawType = ((ParameterizedType) genericSuperclass).getRawType();
					if (baseClass == rawType) {
						q.offerLast((ParameterizedType) genericSuperclass);
						break search;
					} else if (rawType instanceof Class && baseClass.isAssignableFrom((Class) rawType)) {
						that = (Class) rawType;
						q.offerLast((ParameterizedType) genericSuperclass);
						continue search;
					}
				} else if (genericSuperclass instanceof Class && baseClass.isAssignableFrom((Class) genericSuperclass)) {
					that = (Class) genericSuperclass;
					continue search;
				}
			}
			//interfaces
			Type[] genericInterfaces = that.getGenericInterfaces();
			if (genericInterfaces != null && genericInterfaces.length > 0) {
				for (Type genericInterface : genericInterfaces) {
					if (baseClass == genericInterface) {
						break search;
					}
					if (genericInterface instanceof ParameterizedType) {
						Type rawType = ((ParameterizedType) genericInterface).getRawType();
						if (baseClass == rawType) {
							q.offerLast((ParameterizedType) genericInterface);
							break search;
						} else if (rawType instanceof Class && baseClass.isAssignableFrom((Class) rawType)) {
							that = (Class) rawType;
							q.offerLast((ParameterizedType) genericInterface);
							continue search;
						}
					} else if (genericInterface instanceof Class && baseClass.isAssignableFrom((Class) genericInterface)) {
						that = (Class) genericInterface;
						continue search;
					}
				}
			}
			// never or error
			break search;
		}
		// endregion

		int i = index;
		for (ParameterizedType t = q.pollLast(); t != null; t = q.pollLast()) {
			Type[] actualTypeArguments = t.getActualTypeArguments();
			if (actualTypeArguments[i] instanceof Class) {
				return (Class) actualTypeArguments[i];
			} else if (actualTypeArguments[i] instanceof WildcardType) {
				return Object.class;
			} else if (actualTypeArguments[i] instanceof ParameterizedType) {
				Type rawType = ((ParameterizedType) actualTypeArguments[i]).getRawType();
				if (rawType instanceof Class) {
					return (Class) rawType;
				} else {
					return Object.class;
				}
			} else if (actualTypeArguments[i] instanceof TypeVariable) {
				for (int j = 0; j < i; j++) {
					if (actualTypeArguments[j] instanceof Class) {
						i--;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 得到指定类型的指定位置的泛型实参
	 *
	 * @param clazz 目标类
	 * @param index 位置
	 * @return
	 */
	public static Class findParameterizedType(Class clazz, int index) {
		Type[] actualTypeArguments = _findParameterizedTypes(clazz);
		if (actualTypeArguments == null || actualTypeArguments.length == 0) {
			return null;
		}
		return (Class) actualTypeArguments[index];
	}

	public static Class firstParameterizedType(Class clazz) {
		return findParameterizedType(clazz, 0);
	}

	public static Class[] findParameterizedTypes(Class clazz) {
		Type[] actualTypeArguments = _findParameterizedTypes(clazz);
		if (actualTypeArguments == null) {
			return null;
		}
		Class[] types = new Class[actualTypeArguments.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = (Class) actualTypeArguments[i];
		}
		return types;
	}

	private static Type[] _findParameterizedTypes(Class clazz) {
		Type parameterizedType = clazz.getGenericSuperclass();
		// CGLUB subclass target object(泛型在父类上)
		if (!(parameterizedType instanceof ParameterizedType)) {
			parameterizedType = clazz.getSuperclass().getGenericSuperclass();
		}
		if (!(parameterizedType instanceof ParameterizedType)) {
			return null;
		}
		return ((ParameterizedType) parameterizedType).getActualTypeArguments();
	}

	public static <T> String getPropertyName(SerializableSupplier<T> getter) {
		return toGetterOrSetterName(getter.method().getName());
	}

	private static String toGetterOrSetterName(String name) {
		if (name.startsWith("get") || name.startsWith("set")) {
			name = name.substring(3);
		} else if (name.startsWith("is")) {
			name = name.substring(2);
		}
		return Introspector.decapitalize(name);
	}

	public static <T> String getPropertyName(SerializableConsumer<T> setter) {
		return toGetterOrSetterName(setter.method().getName());
	}

	public static <T, R> String getPropertyName(SerializableFunction<T, R> getter) {
		return toGetterOrSetterName(getter.method().getName());
	}

	public static interface SerializableFunction<T, R> extends Function<T, R>, Serializable, MethodReferenceReflection {
	}

	public static interface SerializableSupplier<T> extends Supplier<T>, Serializable, MethodReferenceReflection {
	}

	public static interface SerializableConsumer<T> extends Consumer<T>, Serializable, MethodReferenceReflection {
	}

	public static interface MethodReferenceReflection extends Serializable {
		default SerializedLambda serialized() {
			try {
				Method replaceMethod = getClass().getDeclaredMethod("writeReplace");
				replaceMethod.setAccessible(true);
				return (SerializedLambda) replaceMethod.invoke(this);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		default Class getContainingClass() {
			try {
				String className = serialized().getImplClass().replaceAll("/", ".");
				return Class.forName(className);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		default Method method() {
			SerializedLambda lambda = serialized();
			Class containingClass = getContainingClass();
			return Arrays.asList(containingClass.getDeclaredMethods())
				.stream()
				.filter(method -> Objects.equals(method.getName(), lambda.getImplMethodName()))//TODO check parameter types to deal with overloads
				.findFirst()
				.orElseThrow(UnableToGuessMethodException::new);
		}

		class UnableToGuessMethodException extends RuntimeException {
		}
	}
}

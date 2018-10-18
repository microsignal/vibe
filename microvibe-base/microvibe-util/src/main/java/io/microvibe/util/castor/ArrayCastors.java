package io.microvibe.util.castor;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

public class ArrayCastors {

	/**
	 * 将对象转换为指定的类型的数组<br>
	 * 1.创建指定类型的目标数组<br>
	 * 2.如果对象不是数组类型，将之作为目标数组的最后一维度的第一个元素<br>
	 * 3.如果对象是数组类型，且维度等于目标数组，将对象各维度元素填充入目标数组对应位置<br>
	 * 4.如果对象是数组类型，且维度大于目标数组，尽量将对象各维度元素填充入目标数组对应位置，目标数组的最后一维度<br>
	 * 的值将取此对象在该位置的数组最后一维度的第一个元素<br>
	 * 如：int[][]{{1,2,3},{11,22,33}} 转为int[]的结果是 int[]{1,11}<br>
	 * 5.如果对象是数组类型，且维度小于目标数组，则目标数组的前N维的数组长度为1，第N维的元素为该对象数组，N是两者维度之差<br>
	 * 如：int[][]{{1,2,3},{11,22,33}} 转为int[][][]的结果是
	 * int[][][]{{{1,2,3},{11,22,33}}}<br>
	 * 6.如果对象是一个集合类型，处理方式同数组<br>
	 *
	 * @param arrayType
	 *            Array类型
	 * @param orig
	 *            值对象
	 * @return 转换后的对象
	 */
	public static Object castToArray(final Class<?> arrayType, final Object orig) {
		Object rs = null;
		try {
			if (orig != null) {
				if (arrayType.isAssignableFrom(orig.getClass())) {
					rs = orig;
				} else {
					final String arrayClassName = arrayType.getName();
					final int idx = arrayClassName.lastIndexOf("[");
					final int dimensionsLength = 1 + idx;
					final Class<?> elementType = getArrayElementType(arrayClassName, dimensionsLength);
					rs = castToArray(elementType, dimensionsLength, orig);
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * 将对象value转换为指定维度的指定类型的数组
	 *
	 * @param elementType
	 *            数组元素类型
	 * @param dimensionsLength
	 *            维度
	 * @param orig
	 *            原对象
	 * @return 新数组对象
	 */
	@SuppressWarnings("rawtypes")
	static Object castToArray(final Class<?> elementType, final int dimensionsLength, final Object orig) {
		assert orig != null;
		final int[] dimensions = (int[]) Array.newInstance(int.class, dimensionsLength);
		Class<? extends Object> origClass = orig.getClass();
		final boolean isArray = origClass.isArray();
		final Object array;
		if (isArray) {
			int origDimensionsLength = getDimensionsLength(origClass);
			if (origDimensionsLength < dimensionsLength) {
				Array.setInt(dimensions, 0, 1);
				array = Array.newInstance(elementType, dimensions);
				Array.set(array, 0, castToArray(elementType, dimensionsLength - 1, orig));
			} else {
				final int length = Array.getLength(orig);
				Array.setInt(dimensions, 0, length);
				array = Array.newInstance(elementType, dimensions);
				if (dimensionsLength > 1) {
					for (int i = 0; i < length; i++) {
						Array.set(array, i, castToArray(elementType, dimensionsLength - 1, Array.get(orig, i)));
					}
				} else {
					for (int i = 0; i < length; i++) {
						Array.set(array, i, Castors.cast(elementType, Array.get(orig, i)));
					}
				}
			}
		} else {
			if (dimensionsLength > 1) {
				Array.setInt(dimensions, 0, 1);
				array = Array.newInstance(elementType, dimensions);
				Array.set(array, 0, castToArray(elementType, dimensionsLength - 1, orig));
			} else {
				if (!elementType.isAssignableFrom(origClass) && Collection.class.isAssignableFrom(origClass)) {
					Collection c = (Collection) orig;
					final int length = c.size();
					Array.setInt(dimensions, 0, length);
					array = Array.newInstance(elementType, dimensions);
					Iterator iter = c.iterator();
					for (int i = 0; i < length; i++) {
						Array.set(array, i, Castors.cast(elementType, iter.next()));
					}
				} else {
					Array.setInt(dimensions, 0, 1);
					array = Array.newInstance(elementType, dimensions);
					Array.set(array, 0, Castors.cast(elementType, orig));
				}
			}
		}
		return array;
	}

	static Class<?> getArrayElementType(final String arrayClassName, final int dimensionsLength)
			throws ClassNotFoundException {
		String arrayElementClassName = arrayClassName.substring(dimensionsLength);
		Class<?> elementType = null;
		if (arrayElementClassName.equals(boolean[].class.getName().substring(1))) {// Z
			elementType = boolean.class;
		} else if (arrayElementClassName.equals(int[].class.getName().substring(1))) {// I
			elementType = int.class;
		} else if (arrayElementClassName.equals(long[].class.getName().substring(1))) {// J
			elementType = long.class;
		} else if (arrayElementClassName.equals(char[].class.getName().substring(1))) {// C
			elementType = char.class;
		} else if (arrayElementClassName.equals(byte[].class.getName().substring(1))) {// B
			elementType = byte.class;
		} else if (arrayElementClassName.equals(short[].class.getName().substring(1))) {// S
			elementType = short.class;
		} else if (arrayElementClassName.equals(double[].class.getName().substring(1))) {// D
			elementType = double.class;
		} else if (arrayElementClassName.equals(float[].class.getName().substring(1))) {// F
			elementType = float.class;
		} else {
			if (arrayElementClassName.startsWith("L")) {
				arrayElementClassName = arrayElementClassName.substring(1);
			}
			if (arrayElementClassName.endsWith(";")) {
				arrayElementClassName = arrayElementClassName.substring(0,
						arrayElementClassName.length() - 1);
			}
			elementType = Class.forName(arrayElementClassName);
		}
		return elementType;
	}

	/**
	 * 获取数组类型的维度
	 *
	 * @param arrayType
	 *            数组类型
	 * @return 维度
	 */
	public static int getDimensionsLength(final Class<?> arrayType) {
		final String arrayClassName = arrayType.getName();
		final int idx = arrayClassName.lastIndexOf("[");
		return 1 + idx;
	}

	/**
	 * 获取数组类型的基本元素类型
	 *
	 * @param arrayType
	 *            数组类型
	 * @return 基本元素类型
	 * @throws ClassNotFoundException
	 *             基本元素类型不存在
	 */
	public static Class<?> getArrayElementType(final Class<?> arrayType) throws ClassNotFoundException {
		final String arrayClassName = arrayType.getName();
		final int idx = arrayClassName.lastIndexOf("[");
		final int dimensionsLength = 1 + idx;
		final Class<?> elementType = getArrayElementType(arrayClassName, dimensionsLength);
		return elementType;
	}
}

package io.microvibe.castor.support;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.Attribute;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import io.microvibe.util.StringUtil;
import io.microvibe.castor.ArrayCastors;
import io.microvibe.castor.Castors;
import io.microvibe.castor.Constants;
import io.microvibe.castor.MarshallException;
import io.microvibe.castor.Marshaller;
import io.microvibe.castor.Marshallers;
import io.microvibe.castor.SimpleMarshaller;
import io.microvibe.castor.annotation.XComplexKey;
import io.microvibe.castor.annotation.XComplexType;
import io.microvibe.castor.annotation.XComplexValue;
import io.microvibe.castor.annotation.XExtendedNames;
import io.microvibe.castor.annotation.XIgnore;
import io.microvibe.castor.annotation.XName;
import io.microvibe.castor.annotation.XRootName;
import io.microvibe.castor.annotation.XSerializable;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class GenericComplexMarshaller<T> extends AbstractComplexMarshaller<T> {

	private String xRootName;
	private Map<String, Field> serialFields = new LinkedHashMap<>();
	private Map<Field, FieldDescription> fieldDescriptions = new LinkedHashMap<>();
	private Map<String, Set<Field>> extendedFields = new LinkedHashMap<>();

	private class FieldDescription {
		String xName;
		Class<?> complexType = null;
		String complexKey = null;
		String complexValue = null;
	}

	public GenericComplexMarshaller(Class<T> type) {
		super(type);
		parseClass(type);
	}

	private void parseClass(Class<T> clazz) {
		XRootName xRootNameAnno = clazz.getAnnotation(XRootName.class);
		if (xRootNameAnno != null) {
			xRootName = xRootNameAnno.value();
		} else {
			xRootName = StringUtil.formatNameAsXmlStyle(StringUtil.formatInstanceName(clazz));
		}
		Class<?> superClass = clazz;
		do {
			Field[] fields = superClass.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);

				int modifiers = field.getModifiers();
				if (Modifier.isFinal(modifiers) || Modifier.isTransient(modifiers)
						|| field.getAnnotation(XIgnore.class) != null) {
					continue;// 不可序列化处理
				}

				boolean serializable = true;
				String name = null;
				Class<?> complexType = null;
				String complexKey = null, complexValue = null;
				String[] extendedNames = null;

				XSerializable xSerializable = field.getAnnotation(XSerializable.class);
				if (xSerializable != null) {
					serializable = xSerializable.value();
				}
				/*if (!serializable) {
					continue;//不可序列化处理
				}*/

				// [start]读注解
				XName xName = field.getAnnotation(XName.class);
				if (xName != null) {
					name = StringUtil.trimToNull(xName.value());
				}
				XComplexKey xComplexKey = field.getAnnotation(XComplexKey.class);
				if (xComplexKey != null) {
					complexKey = StringUtil.trimToNull(xComplexKey.value());
				}
				XComplexValue xComplexValue = field.getAnnotation(XComplexValue.class);
				if (xComplexValue != null) {
					complexValue = StringUtil.trimToNull(xComplexValue.value());
				}
				XComplexType xComplexType = field.getAnnotation(XComplexType.class);
				if (xComplexType != null) {
					complexType = xComplexType.value();
				}
				XExtendedNames xExtendedNames = field.getAnnotation(XExtendedNames.class);
				if (xExtendedNames != null) {
					List<String> list = Arrays.asList(xExtendedNames.value());
					removeEmptyOnes(list);
					if (list.size() > 0) {
						extendedNames = new String[list.size()];
						list.toArray(extendedNames);
					}
				}
				// [end]读注解

				if (name == null) {
					name = StringUtil.formatNameAsXmlStyle(field.getName());
				}
				if (complexType == null) {
					Class<?> fieldType = field.getType();
					if (fieldType.isArray()) {
						try {
							Class<?> arrayElementType = ArrayCastors.getArrayElementType(fieldType);
							if (isParseableComplexType(arrayElementType)) {
								complexType = arrayElementType;// FIXME test
							}
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					} else if (Collection.class.isAssignableFrom(fieldType)) {
						Type genericType = field.getGenericType();
						if (genericType instanceof ParameterizedType) {
							ParameterizedType pt = (ParameterizedType) genericType;
							Type[] actualTypeArguments = pt.getActualTypeArguments();
							if (actualTypeArguments.length == 1) {
								if (actualTypeArguments[0] instanceof Class) {
									if (isParseableComplexType((Class) actualTypeArguments[0])) {
										complexType = (Class) actualTypeArguments[0];// FIXME test
									}
								}
							}
						}
					} else if (Map.class.isAssignableFrom(fieldType)) {
						Type genericType = field.getGenericType();
						if (genericType instanceof ParameterizedType) {
							ParameterizedType pt = (ParameterizedType) genericType;
							Type[] actualTypeArguments = pt.getActualTypeArguments();
							if (actualTypeArguments.length == 2) {
								if (actualTypeArguments[0] instanceof Class) {
									Class actualKeyType = (Class) actualTypeArguments[0];
									if (complexKey == null
											&& isParseableComplexType(actualKeyType)) {
										complexType = actualKeyType;
									}
								}
								if (actualTypeArguments[1] instanceof Class) {
									Class actualValueType = (Class) actualTypeArguments[1];
									if (complexType == null && complexValue == null
											&& isParseableComplexType(actualValueType)) {
										complexType = actualValueType;
									}
								}
							}
						}
					} else if (isParseableComplexType(fieldType)) {
						complexType = fieldType;
					}
				}
				if (complexType == Object.class) {
					complexType = null;
				}
				// 序列化字段
				if (serializable && !serialFields.containsKey(name)) {
					FieldDescription fieldDescription = new FieldDescription();
					fieldDescription.xName = name;
					if (complexType != null) {
						fieldDescription.complexKey = complexKey;
						fieldDescription.complexValue = complexValue;
						fieldDescription.complexType = complexType;
					}
					serialFields.put(name, field);
					fieldDescriptions.put(field, fieldDescription);
				}
				// 扩展字段
				if (extendedNames != null) {
					for (String extendedName : extendedNames) {
						Set<Field> set = extendedFields.get(extendedName);
						if (set == null) {
							set = new LinkedHashSet<>();
							extendedFields.put(extendedName, set);
						}
						set.add(field);
					}
				}

			}
			superClass = superClass.getSuperclass();
		} while (superClass != null && superClass != Object.class);
	}

	private static void removeEmptyOnes(List<String> list) {
		list.removeIf(s -> s == null || s.length() == 0);
	}

	private static boolean isParseableComplexType(Class type) {
		return type.isEnum() || (!type.isPrimitive() && !type.isArray()
				&& !Modifier.isAbstract(type.getModifiers())
				&& Marshallers.getSimpleMarshaller(type) == null);
	}

	@Override
	protected String defaultRootNodeName(T obj) {
		return xRootName;
	}

	@Override
	protected void innerMarshal(T obj, Element container, String nodeName) {
		nodeName = StringUtil.trimToNull(nodeName);

		if (handleMarshalledRef(obj, container, nodeName)) {
			return;
		}
		cacheMarshalledObj(obj, container, nodeName);

		if (nodeName != null) {
			Element node = DocumentHelper.createElement(nodeName);
			container.add(node);
			container = node;
		}

		Set<Entry<Field, FieldDescription>> entrySet = fieldDescriptions.entrySet();
		for (Entry<Field, FieldDescription> entry : entrySet) {
			try {
				Field field = entry.getKey();
				FieldDescription desc = entry.getValue();
				Object fieldValue = field.get(obj);
				if (fieldValue != null) {
					String fieldXName = desc.xName;
					Class complexType = desc.complexType;
					String complexKey = desc.complexKey;
					String complexValue = desc.complexValue;
					Class fieldClass = fieldValue.getClass();

					if (complexType != null) {// 复合类型
						if (fieldValue instanceof Map) {
							// Map类型字段处理
							__marshalMapComplexType(container, fieldValue, fieldXName, complexType, complexKey, complexValue);
						} else if (fieldValue instanceof Collection) {
							// 集合类型处理
							__marshalCollectionComplexType(container, fieldValue, fieldXName, complexType, complexValue);
						} else if (fieldClass.isArray()) {
							// 数组类型处理(支持多维)
							__marshalArrayComplexType__(container, fieldValue, fieldXName, complexType, complexValue);
						} else {
							// 其他类型(枚举、常规类等)处理
							__marshalComplexType__(container, fieldValue, fieldXName, complexType, complexValue);
						}
					} else {// 简单类型
						Marshallers.getMarshaller(fieldClass).marshal(fieldValue, container, fieldXName);
					}
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				//e.printStackTrace();// log
				throw new MarshallException(e);
			} catch (InstantiationException e) {
				//e.printStackTrace();// log
				throw new MarshallException(e);
			}
		}
	}

	private void __marshalMapComplexType(Element container, Object fieldValue, String fieldXName, Class complexType, String complexKey, String complexValue)
			throws InstantiationException, IllegalAccessException {
		Set<Entry> fieldValueEntrySet = ((Map) fieldValue).entrySet();
		Collection collection = new ArrayList();
		for (Entry fieldValueEntry : fieldValueEntrySet) {
			Object complexObj = null;
			if (complexKey == null) {
				if (fieldValueEntry.getKey() != null) {
					complexObj = Castors.cast(complexType, fieldValueEntry.getKey());
				}
			}
			if (complexObj == null && complexValue == null) {
				if (fieldValueEntry.getValue() != null) {
					complexObj = Castors.cast(complexType, fieldValueEntry.getValue());
				}
			}
			if (complexObj == null) {
				complexObj = complexType.newInstance();
				if (complexKey != null) setField(complexObj, complexKey, fieldValueEntry.getKey());
				if (complexValue != null) setField(complexObj, complexValue, fieldValueEntry.getValue());
			}
			collection.add(complexObj);
		}
		Marshallers.getMarshaller(Collection.class).marshal(collection, container, fieldXName);
	}

	private void __marshalCollectionComplexType(Element container, Object fieldValue, String fieldXName, Class complexType, String complexValue)
			throws InstantiationException, IllegalAccessException {
		Collection collection = new ArrayList();
		for (Object iFieldValue : (Collection<?>) fieldValue) {
			Object complexObj;
			if (complexValue == null) {
				complexObj = Castors.cast(complexType, iFieldValue);
			} else {
				complexObj = complexType.newInstance();
				setField(complexObj, complexValue, iFieldValue);
			}
			collection.add(complexObj);
		}
		Marshallers.getMarshaller(Collection.class).marshal(collection, container, fieldXName);
	}

	private void __marshalArrayComplexType__(Element container, Object fieldArrayValue, String fieldXName, Class complexType, String complexValue)
			throws InstantiationException, IllegalAccessException {
		int length = Array.getLength(fieldArrayValue);
		for (int i = 0; i < length; i++) {
			Object iFieldValue = Array.get(fieldArrayValue, i);
			Element arrayEle = DocumentHelper.createElement(fieldXName);
			container.add(arrayEle);
			if (iFieldValue != null) {
				if (iFieldValue.getClass().isArray()) {
					__marshalArrayComplexType__(arrayEle,
							iFieldValue, Constants.ARRAY_NODE_NAME, complexType, complexValue);
				} else {
					Object complexObj;
					if (complexValue == null) {
						complexObj = Castors.cast(complexType, iFieldValue);
					} else {
						complexObj = complexType.newInstance();
						setField(complexObj, complexValue, iFieldValue);
					}
					Marshallers.getMarshaller(complexType).marshal(complexObj, arrayEle, null);
				}
			}
		}
	}

	private void __marshalComplexType__(Element container, Object fieldValue, String fieldXName, Class complexType, String complexValue)
			throws InstantiationException, IllegalAccessException {
		Object complexObj;
		if (complexValue == null) {
			complexObj = Castors.cast(complexType, fieldValue);
		} else {
			complexObj = complexType.newInstance();
			setField(complexObj, complexValue, fieldValue);
		}
		Marshallers.getMarshaller(complexType).marshal(complexObj, container, fieldXName);
	}

	// --------------------------------------------------------------------------
	// --------------------------------------------------------------------------

	@Override
	protected T createInstance() throws MarshallException {
		try {
			T t = type.newInstance();
			return t;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new MarshallException(e);
		}
	}

	@Override
	public T innerUnmarshalSpecifiedNode(Element container, String nodeName, T obj) {
		nodeName = StringUtil.trimToNull(nodeName);

		Object ref = findUnmarshalledRef(container, nodeName);
		if (ref != null) {
			return (T) ref;
		}
		cacheUnmarshalledObj(container, nodeName, obj);

		String containerValue = null;
		if (nodeName != null) {
			Element node = container.element(nodeName);
			if (node == null) {
				containerValue = StringUtil.trimToNull(container.attributeValue(nodeName));
			}
			container = node;
			cacheUnmarshalledObj(container, null, obj);
		}

		if (container != null) {
			// attributes
			List<Attribute> attrList = container.attributes();
			for (Attribute attr : attrList) {
				setFieldValueOfAttribute(obj, attr);
			}
			// text
			{
				containerValue = StringUtil.trimToNull(container.getText());
			}
			// elements
			List<Element> eleList = container.elements();
			Map<String, List<Element>> elements = new LinkedHashMap<>();
			for (Element ele : eleList) {
				String serialName = ele.getName();
				List<Element> list = elements.get(serialName);
				if (list == null) {
					elements.put(serialName, list = new ArrayList<Element>());
				}
				list.add(ele);
			}
			Set<Entry<String, List<Element>>> entrySet = elements.entrySet();
			for (Entry<String, List<Element>> entry : entrySet) {
				try {
					setFieldValueOfElement(obj, container, entry.getKey(), entry.getValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (containerValue != null) {
//			setField(obj, Constants.VALUE_NODE_NAME, containerValue);
			setFieldValueOfValueable(obj, containerValue);
		}
		return obj;
	}

	private void setFieldValueOfValueable(final T object, String value) {
		if (value == null) {
			return;
		}
		String serialName = Constants.VALUE_NODE_NAME;
		{ //serial
			Map<String, Field> serialFields = this.serialFields;
			Field field = serialFields.get(serialName);
			if (field == null) {
				field = serialFields.get(StringUtil.formatNameAsJavaStyle(serialName));
			}
			if (field != null) {
				field.setAccessible(true);
				try {
					field.set(object, Castors.cast(field.getType(), value));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		{ // extends
			Map<String, Set<Field>> extendedFields = this.extendedFields;
			Set<Field> fields = extendedFields.get(serialName);
			if (fields == null) {
				fields = extendedFields.get(StringUtil.formatNameAsJavaStyle(serialName));
			}
			if (fields != null) {
				for (Field field : fields) {
					field.setAccessible(true);
					try {
						field.set(object, Castors.cast(field.getType(), value));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void setFieldValueOfAttribute(final T object, final Attribute attr) {
		String value = StringUtil.trimToNull(attr.getValue());
		if (value == null) {
			return;
		}
		String serialName = attr.getName();
		{ //serial
			Map<String, Field> serialFields = this.serialFields;
			Field field = serialFields.get(serialName);
			if (field == null) {
				field = serialFields.get(StringUtil.formatNameAsJavaStyle(serialName));
			}
			if (field != null) {
				field.setAccessible(true);
				try {
					field.set(object, Castors.cast(field.getType(), value));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		{ // extends
			Map<String, Set<Field>> extendedFields = this.extendedFields;
			Set<Field> fields = extendedFields.get(serialName);
			if (fields == null) {
				fields = extendedFields.get(StringUtil.formatNameAsJavaStyle(serialName));
			}
			if (fields != null) {
				for (Field field : fields) {
					field.setAccessible(true);
					try {
						field.set(object, Castors.cast(field.getType(), value));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void setFieldValueOfElement(T object, Element container, String serialName, List<Element> eleList)
			throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException, InstantiationException {

		{ //serial
			Map<String, Field> serialFields = this.serialFields;
			Field field = serialFields.get(serialName);
			if (field == null) {
				field = serialFields.get(StringUtil.formatNameAsJavaStyle(serialName));
			}
			if (field != null) {
				Object fieldValue = createFieldValueOfElement(object, field, container, serialName, eleList);
				field.set(object, fieldValue);
			}
		}
	}

	private Object createFieldValueOfElement(T object, Field field, Element container, String serialName, List<Element> eleList)
			throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException, InstantiationException {
		Map<Field, FieldDescription> fieldDescriptions = this.fieldDescriptions;
		field.setAccessible(true);
		Object defValue = field.get(object);
		Class fieldType = field.getType();

		FieldDescription desc = fieldDescriptions.get(field);
		Class complexType = desc.complexType;
		String complexKey = desc.complexKey;
		String complexValue = desc.complexValue;

//		Field complexKeyField = getDeclaredField(complexType, complexKey);
//		Field complexValueField = getDeclaredField(complexType, complexValue);

		// Array Type
		if (fieldType.isArray()) {
			Class arrayElementType = ArrayCastors.getArrayElementType(fieldType);
			int dimensionsLength = ArrayCastors.getDimensionsLength(fieldType);
			if (complexType == null) {
				return createArray(arrayElementType, dimensionsLength,
						container, serialName, eleList);
			} else {
				Object array = createArray(complexType, dimensionsLength, container, serialName, eleList);
				if (complexValue == null) {
					return Castors.cast(fieldType, array);
				} else {
					return createMappedArray(array, getDeclaredField(complexType, complexValue),
							dimensionsLength, arrayElementType);
				}
			}
		}
		// Collection Type
		else if (Collection.class.isAssignableFrom(fieldType)) {
			return createCollection(fieldType, (Collection) defValue, complexType, complexValue,
					container, serialName, eleList);
		}
		// Map Type
		else if (Map.class.isAssignableFrom(fieldType)) {
			return createMap(fieldType, (Map) defValue, complexType, complexKey, complexValue,
					container, serialName, eleList);
		}
		// Other Type
		else {
			return createObject(fieldType, defValue, complexType, complexValue,
					container, serialName, eleList);
		}
	}

	private Object createObject(Class fieldType, Object defValue, Class complexType, String complexValue,
			Element container, String serialName, List<Element> eleList) throws IllegalAccessException {
		int eleCount = eleList.size();
		if (eleCount == 1) {
			Object refObj = findUnmarshalledRef(eleList.get(0), null);
			if (refObj != null) {
				return Castors.cast(fieldType, refObj);
			}
		}

		Object target = defValue;

		if (eleCount > 0) {
			Element ele = eleList.get(0);// 只读取第一个元素
			if (complexType == null) {
				SimpleMarshaller simpleMarshaller = Marshallers.getSimpleMarshaller(fieldType);
				if (simpleMarshaller != null) {
					target = simpleMarshaller.unmarshal(ele, (String) null);
				}
			} else {
				Marshaller marshaller = Marshallers.getMarshaller(complexType);
				Object unmarshalled = marshaller.unmarshal(ele, (String) null);
				if (complexValue != null) {
					Field complexValueField = getDeclaredField(complexType, complexValue);
					complexValueField.setAccessible(true);
					target = complexValueField.get(unmarshalled);
				} else {
					target = unmarshalled;
				}
			}
		}
		return target;
	}

	private Object createMap(Class fieldType, Map defValue,
			Class complexType, String complexKey, String complexValue,
			Element container, String serialName, List<Element> eleList) throws IllegalAccessException {
		int eleCount = eleList.size();
		if (eleCount == 1) {
			Object refObj = findUnmarshalledRef(eleList.get(0), null);
			if (refObj != null) {
				//return Castors.cast(fieldType, refObj);
				Class refClass = refObj.getClass();
				if (fieldType.isAssignableFrom(refClass)) {
					return refObj;
				} else {
					if (complexType == null) {
						return defValue;
					}
					Map map = defValue;
					if (map == null) {
						map = tryCreateMap(fieldType);
					}
					if (map == null) {
						return defValue;
					}
					Field complexKeyField = null;
					if (complexKey != null) {
						complexKeyField = getDeclaredField(refClass, complexKey);
						complexKeyField.setAccessible(true);
					}
					Field complexValueField = null;
					if (complexValue != null) {
						complexValueField = getDeclaredField(refClass, complexValue);
						complexValueField.setAccessible(true);
					}
					Object key = refObj, value = refObj;
					if (complexKeyField != null) {
						key = complexKeyField.get(refObj);
					}
					if (complexValueField != null) {
						value = complexValueField.get(refObj);
					}
					map.put(key, value);
					return map;
				}
			}
		}

		Map map = defValue;
		if (map == null) {
			map = tryCreateMap(fieldType);
		}

		if (map != null) {
			cacheUnmarshalledObj(container, serialName, map);

			// map.clear();//初始元素清除步骤
			if (complexType == null) {
				Marshaller<String> marshaller = Marshallers.getMarshaller(String.class);
				for (Element ele : eleList) {
					String key = marshaller
							.unmarshal(ele, Constants.KEY_NODE_NAME);
					String value = marshaller
							.unmarshal(ele, Constants.VALUE_NODE_NAME);
					map.put(key, value);
				}
			} else {
				Field complexKeyField = null;
				if (complexKey != null) {
					complexKeyField = getDeclaredField(complexType, complexKey);
					complexKeyField.setAccessible(true);
				}
				Field complexValueField = null;
				if (complexValue != null) {
					complexValueField = getDeclaredField(complexType, complexValue);
					complexValueField.setAccessible(true);
				}
				Marshaller marshaller = Marshallers.getMarshaller(complexType);
				for (Element ele : eleList) {
					Object one = marshaller.unmarshal(ele, (String) null);
					Object key = one, value = one;
					if (complexKeyField != null) {
						key = complexKeyField.get(one);
					}
					if (complexValueField != null) {
						value = complexValueField.get(one);
					}
					map.put(key, value);
				}
			}
		}
		return map;
	}

	private Map tryCreateMap(Class<Map> fieldType) {
		try {
			if (!Modifier.isAbstract(fieldType.getModifiers())) {
				return fieldType.newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (fieldType.isAssignableFrom(LinkedHashMap.class)) {
			return new LinkedHashMap<>();
		} else if (fieldType.isAssignableFrom(HashMap.class)) {
			return new HashMap<>();
		} else if (fieldType.isAssignableFrom(TreeMap.class)) {
			return new TreeMap<>();
		} else if (fieldType.isAssignableFrom(ConcurrentHashMap.class)) {
			return new ConcurrentHashMap<>();
		} else if (fieldType.equals(Properties.class)) {
			return new Properties();
		} else if (fieldType.isAssignableFrom(Hashtable.class)) {
			return new Hashtable<>();
		} else {
			return null;
		}
	}

	private Object createCollection(Class fieldType, Collection defValue,
			Class complexType, String complexValue,
			Element container, String serialName, List<Element> eleList) throws IllegalAccessException {
		int eleCount = eleList.size();
		if (eleCount == 1) {
			Object refObj = findUnmarshalledRef(eleList.get(0), null);
			if (refObj != null) {
				return Castors.cast(fieldType, refObj);
			}
		}

		Collection collection = defValue;
		if (collection == null) {
			collection = tryCreateCollection(fieldType);
		}
		if (collection != null) {
			cacheUnmarshalledObj(container, serialName, collection);

			// collection.clear();//初始元素清除步骤
			Field complexValueField = null;
			if (complexType == null) {
				complexType = String.class;
			} else if (complexValue != null) {
				complexValueField = getDeclaredField(complexType, complexValue);
				complexValueField.setAccessible(true);
			}
			Marshaller marshaller = Marshallers.getMarshaller(complexType);
			for (Element ele : eleList) {
				Object one = marshaller.unmarshal(ele, (String) null);
				if (complexValueField != null) {
					one = complexValueField.get(one);
				}
				collection.add(one);
			}
		}
		return collection;
	}

	private Collection tryCreateCollection(Class<Collection> fieldType) {
		try {
			if (!Modifier.isAbstract(fieldType.getModifiers())) {
				return fieldType.newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (fieldType.isAssignableFrom(ArrayList.class)) {
			return new ArrayList<>();
		} else if (fieldType.isAssignableFrom(LinkedHashSet.class)) {
			return new LinkedHashSet<>();
		} else if (fieldType.isAssignableFrom(HashSet.class)) {
			return new HashSet<>();
		} else if (fieldType.isAssignableFrom(TreeSet.class)) {
			return new TreeSet<>();
		} else if (fieldType.isAssignableFrom(ArrayDeque.class)) {
			return new ArrayDeque<>();
		} else if (fieldType.isAssignableFrom(ArrayBlockingQueue.class)) {
			return new ArrayBlockingQueue<>(16);
		} else if (fieldType.isAssignableFrom(LinkedList.class)) {
			return new LinkedList<>();
		} else if (fieldType.isAssignableFrom(Stack.class)) {
			return new Stack<>();
		} else {
			return null;
		}
	}

	private Object createMappedArray(Object orig, Field complexValueField, int dimensionsLength, Class arrayElementType)
			throws ArrayIndexOutOfBoundsException, IllegalArgumentException, IllegalAccessException {
		int length = Array.getLength(orig);
		int[] dimensions = new int[dimensionsLength];
		dimensions[0] = length;
		Object array = Array.newInstance(arrayElementType, dimensions);
		for (int i = 0; i < length; i++) {
			Object iOrig = Array.get(orig, i);
			if (iOrig != null) {
				if (dimensionsLength == 1) {
					complexValueField.setAccessible(true);
					Array.set(array, i, Castors.cast(arrayElementType, complexValueField.get(iOrig)));
				} else if (iOrig.getClass().isArray()) {
					createMappedArray(iOrig, complexValueField, dimensionsLength - 1, arrayElementType);
				}
			}
		}
		return array;
	}

	private Object createArray(Class arrayElementType, int dimensionsLength, Element container, String serialName, List<Element> eleList)
			throws InstantiationException, IllegalAccessException {
		int[] dimensions = new int[dimensionsLength];
		int eleCount = eleList.size();
		dimensions[0] = eleCount;
		Object array = Array.newInstance(arrayElementType, dimensions);

		if (eleCount == 1) {
			Object refObj = findUnmarshalledRef(eleList.get(0), null);
			if (refObj != null) {
				return Castors.cast(array.getClass(), refObj);
			}
		}
		cacheUnmarshalledObj(container, serialName, array);

		int i = 0;
		for (Element ele : eleList) {
			if (dimensionsLength == 1) {
				Object iArray = Marshallers.getMarshaller(arrayElementType)
						.unmarshal(ele, (String) null);
				Array.set(array, i, iArray);
			} else {
				List<Element> iEleList = ele.elements(Constants.ARRAY_NODE_NAME);
				Object iArray = createArray(arrayElementType, dimensionsLength - 1, ele, Constants.ARRAY_NODE_NAME, iEleList);
				Array.set(array, i, iArray);
			}
			i++;
		}
		return array;
	}

	public static Object setField(Object bean, String fieldName, Object value) {
		Field field = getDeclaredField(bean.getClass(),
				fieldName);
		if (field != null) {
			try {
				field.setAccessible(true);
				Object fieldValue = value == null ? null : Castors.cast(field.getType(), value);
				field.set(bean, fieldValue);
				return fieldValue;
			} catch (SecurityException | IllegalArgumentException
					| IllegalAccessException e) {
			}
		}
		return null;
	}

	public static Field getDeclaredField(Class clazz, String fieldName) {
		Field field = null;
		Class<?> superClass = clazz;
		do {
			try {
				field = superClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException | SecurityException e) {
			}
			if (field != null) {
				break;
			}
			superClass = superClass.getSuperclass();
		} while (superClass != null && superClass != Object.class);
		return field;
	}
}

package io.microvibe.util.castor;

import java.util.Map;

import io.microvibe.util.env.Env;

/**
 * {@inheritDoc}
 *
 * @author Qt
 * @since Java 1.8 , 2016年8月10日
 */
@SuppressWarnings("rawtypes")
public interface Constants {

	/** 是否优先使用属性而非子元素组织xml结构 */
	boolean MARSHAL_ATTRIBUTE_PRIOR = Boolean.valueOf(Env.get("marshal.attr.prior", "true"));

	String ROOT_NODE_NAME = "xml";

	// 引用类型的元素名称
	String XREF_NODE_NAME = "xref";
	String XREF_XPATH_ATTR_NAME = "xpath";
	String XREF_XNODE_ATTR_NAME = "xnode";

	String KEY_NODE_NAME = "key";
	String VALUE_NODE_NAME = "value";
	String ENTRY_NODE_NAME = "entry";
	String ARRAY_NODE_NAME = "array";
	String OBJECT_NODE_NAME = "object";

	Class DEFAULT_MAP_COMPLEX_TYPE = DefaultMapEntry.class;
	String DEFAULT_MAP_COMPLEX_KEY = "key";
	String DEFAULT_MAP_COMPLEX_VALUE = "value";

	class DefaultMapEntry<K, V> implements Map.Entry<K, V> {
		private K key;
		private V value;

		@Override
		public K getKey() {
			return this.key;
		}
		@Override
		public V getValue() {
			return this.value;
		}
		@Override
		public V setValue(V value) {
			V old = this.value;
			this.value = value;
			return old;
		}
	}
}

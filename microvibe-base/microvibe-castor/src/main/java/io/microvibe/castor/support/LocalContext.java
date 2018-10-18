package io.microvibe.castor.support;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Element;

public class LocalContext {

	private static final ThreadLocal<LocalContext> local = new ThreadLocal<LocalContext>();

	public static LocalContext getLocalContext() {
		return local.get();
	}

	public static LocalContext bind() {
		LocalContext context = new LocalContext();
		local.set(new LocalContext());
		return context;
	}

	public static void bind(LocalContext context) {
		local.set(context);
	}

	public static void unbind() {
		local.remove();
	}

	public static class ElementCache {
		private Element container;
		private String nodeName;

		public ElementCache(Element container, String nodeName) {
			this.container = container;
			this.nodeName = nodeName;
		}

		public Element getContainer() {
			return container;
		}

		public String getNodeName() {
			return nodeName;
		}
	}

	/** 序列化为xml时的对象缓存 */
	private final ObjectCache<Object, ElementCache> writtenObjectCache = new ObjectCache<Object, ElementCache>();
	/** 当前已反序列化解析到的类对象的缓存 */
	private final Map<Element, Map<String, Object>> readObjectCache = new HashMap<>();

	public void addMarshalledObjectCache(Object obj, Element container, String nodeName) {
		writtenObjectCache.add(obj, new ElementCache(container, nodeName));
	}

	public ElementCache getMarshalledObjectCache(Object obj) {
		return writtenObjectCache.get(obj);
	}

	public void addUnmarshalledObjectCache(Element container, String nodeName, Object obj) {
		Map<Element, Map<String, Object>> readObjectCache = this.readObjectCache;
		Map<String, Object> map = readObjectCache.get(container);
		if (map == null) {
			map = new HashMap<>();
			readObjectCache.put(container, map);
		}
		map.put(nodeName, obj);
	}

	public Object getUnmarshalledObjectCache(Element container, String nodeName) {
		Map<Element, Map<String, Object>> readObjectCache = this.readObjectCache;
		Map<String, Object> map = readObjectCache.get(container);
		if (map != null) {
			return map.get(nodeName);
		}
		return null;
	}

}

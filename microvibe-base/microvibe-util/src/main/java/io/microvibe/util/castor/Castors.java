package io.microvibe.util.castor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import io.microvibe.util.io.IOUtil;
import io.microvibe.util.castor.support.AnyArrayCastor;
import io.microvibe.util.castor.support.AnyCollectionCastor;
import io.microvibe.util.castor.support.AnyEnumCastor;
import io.microvibe.util.castor.support.BooleanCastor;
import io.microvibe.util.castor.support.ByteCastor;
import io.microvibe.util.castor.support.CharacterCastor;
import io.microvibe.util.castor.support.DoubleCastor;
import io.microvibe.util.castor.support.FloatCastor;
import io.microvibe.util.castor.support.IntegerCastor;
import io.microvibe.util.castor.support.LongCastor;
import io.microvibe.util.castor.support.NullCastor;
import io.microvibe.util.castor.support.ShortCastor;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Castors {

	private static final Lock lock = new ReentrantLock();
	private static Map<Class<?>, Castor> castors = Collections.synchronizedMap(new HashMap<>());
	private static Map<Class<?>, SimpleMarshaller> simpleMarshallers = Collections.synchronizedMap(new HashMap<>());
	private static Map<Class<?>, Castor> runtimeCastors = Collections.synchronizedMap(new WeakHashMap<>());
	static {
		reloadCastorConfig();
	}

	public static void reloadCastorConfig() {
		String path = System.getProperty("castor.config", "/castor-config.xml");
		reloadCastorConfig(path);
	}

	public static void reloadCastorConfig(String path) {
		lock.lock();
		try {
			Map<Class<?>, Castor> castors = Collections.synchronizedMap(new HashMap<>());
			castors.put(null, new NullCastor(Object.class));
			castors.put(boolean.class, new BooleanCastor(boolean.class));
			castors.put(int.class, new IntegerCastor(int.class));
			castors.put(long.class, new LongCastor(long.class));
			castors.put(byte.class, new ByteCastor(byte.class));
			castors.put(short.class, new ShortCastor(short.class));
			castors.put(char.class, new CharacterCastor(char.class));
			castors.put(double.class, new DoubleCastor(double.class));
			castors.put(float.class, new FloatCastor(float.class));

			InputStream in = IOUtil.getInputStream(path, Castors.class);
			SAXReader reader = new SAXReader(false);
			reader.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId,
						String systemId) throws SAXException, IOException {
					InputSource is = new InputSource(new StringReader(""));
					is.setPublicId(publicId);
					is.setSystemId(systemId);
					return is;
				}
			});
			Document doc = reader.read(in);
			Element root = doc.getRootElement();
			for (Iterator<Element> iter = root.elementIterator("castor"); iter.hasNext();) {
				Element castorElement = iter.next();
				String typename = castorElement.attributeValue("type");
				String classname = castorElement.attributeValue("class");
				try {
					Class<?> type = Class.forName(typename);
					Class<Castor> clazz = (Class<Castor>) Class.forName(classname);
					Constructor<Castor> constructor = clazz.getConstructor(Class.class);
					Castor castor = constructor.newInstance(type);
					castors.put(type, castor);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			Map<Class<?>, Castor> _castors = Castors.castors;
			Castors.castors = castors;
			_castors.clear();
			final Map<Class<?>, SimpleMarshaller> simpleMarshallers = Collections.synchronizedMap(new HashMap<>());
			castors.forEach((type, castor) -> {
				if (castor instanceof SimpleMarshaller) {
					simpleMarshallers.put(type, (SimpleMarshaller) castor);
				}
			});
			Map<Class<?>, SimpleMarshaller> _simpleMarshallers = Castors.simpleMarshallers;
			Castors.simpleMarshallers = simpleMarshallers;
			_simpleMarshallers.clear();
			Castors.runtimeCastors.clear();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public static <T> SimpleMarshaller<T> getSimpleMarshaller(Class<T> type) {
		return simpleMarshallers.get(type);
	}

	public static <T> Castor<T> getCastor(Class<T> type) {
		Castor<T> castor = castors.get(type);
		if (castor == null) {
			castor = runtimeCastors.get(type);
		}
		return castor;
	}

	public static <T> T castQuietly(Class<T> type, Object orig) {
		try {
			return cast(type, orig);
		} catch (Exception e) {
			return null;
		}
	}

	public static <T> T cast(Class<T> type, Object orig) {
		if (orig == null) return null;
		if (type.isAssignableFrom(orig.getClass())) {
			return (T) orig;
		}
		Castor<T> castor = getCastor(type);
		if (castor != null) {
			return castor.cast(orig);
		} else {
			if (type.isArray()) {
				runtimeCastors.put(type, castor = new AnyArrayCastor(type));
				return castor.cast(orig);
			} else if (Collection.class.isAssignableFrom(type)) {
				runtimeCastors.put(type, castor = new AnyCollectionCastor(type));
				return castor.cast(orig);
			} else if (type.isEnum()) {
				runtimeCastors.put(type, castor = new AnyEnumCastor(type));
				return castor.cast(orig);
			} else {
				return type.cast(orig); //throw new ClassCastException();
			}
		}
	}
}

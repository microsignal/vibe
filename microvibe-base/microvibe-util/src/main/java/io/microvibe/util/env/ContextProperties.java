package io.microvibe.util.env;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import io.microvibe.util.io.IOUtil;

public class ContextProperties {
	static class Entry {
		Properties props;
		boolean isFile = false;
		String filePath;
		long lastModified = 0L;
	}
	private Map<String, Entry> entrys =
			Collections.synchronizedMap(new LinkedHashMap<String, Entry>());
	private String charset = Charset.defaultCharset().name();

	public Properties getProperties(String resource) {
		if (resource == null) {
			return null;
		}
		Entry entry = entrys.get(resource);
		if (entry == null) {
			entry = new Entry();
			entrys.put(resource, entry);
			URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
			if (url != null) {
				entry.props = new Properties();
				entry.filePath = decodeURL(url);
				File file = new File(entry.filePath);
				if (file.exists()) {
					entry.isFile = true;
					entry.lastModified = file.lastModified();
				}
				InputStream in = null;
				try {
					in = url.openStream();
					if (resource.endsWith(".xml")) {
						entry.props.loadFromXML(in);
					} else {
						entry.props.load(in);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					IOUtil.close(in);
				}
			}
		} else {
			if (entry.isFile) {
				File file = new File(entry.filePath);
				if (file.lastModified() > entry.lastModified) {
					entry.lastModified = file.lastModified();
					InputStream in = null;
					try {
						in = new FileInputStream(file);
						if (resource.endsWith(".xml")) {
							entry.props.loadFromXML(in);
						} else {
							entry.props.load(in);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						IOUtil.close(in);
					}
				}
			}
		}
		return entry.props;
	}

	public boolean containsKey(String resource, String key){
		Properties properties = getProperties(resource);
		if (properties == null) {
			return false;
		}
		return properties.containsKey(key);
	}
	public String getProperty(String resource, String key) {
		Properties properties = getProperties(resource);
		if (properties == null) {
			return null;
		}
		return properties.getProperty(key);
	}

	public void setDefaultCharset(String charset) {
		this.charset = charset;
	}

	private String decodeURL(URL url) {
		try {
			return URLDecoder.decode(url.getFile(), charset);
		} catch (UnsupportedEncodingException e) {
			return url.getFile();
		}
	}

}

package io.microvibe.util.compile;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class MemoryClassLoader extends URLClassLoader {

	private MemoryClassLoader() {
		super(new URL[0]);
	}

	public static MemoryClassLoader newInstance() {
		MemoryClassLoader mcl = new MemoryClassLoader();
		return mcl;
	}

	public static String getClassPath(String... paths) {
		StringBuilder classpath = new StringBuilder();
		ClassLoader cl = MemoryClassLoader.class.getClassLoader();
		if (cl instanceof URLClassLoader) {
			for (URL url : ((URLClassLoader) cl).getURLs()) {
				String path = url.getFile();
				classpath.append(path).append(File.pathSeparator);
			}
		}
		for (String path : paths) {
			classpath.append(path).append(File.pathSeparator);
		}
		classpath.append(System.getProperty("java.class.path"));
		return classpath.toString();
	}

	public Class<?> defineMemoryClass(String name, byte[] b) {
		return super.defineClass(name, b, 0, b.length);
	}

}

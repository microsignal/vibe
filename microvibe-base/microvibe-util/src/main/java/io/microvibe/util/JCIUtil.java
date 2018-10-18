package io.microvibe.util;

import org.apache.commons.jci.compilers.CompilationResult;
import org.apache.commons.jci.compilers.JavaCompiler;
import org.apache.commons.jci.compilers.JavaCompilerFactory;
import org.apache.commons.jci.readers.MemoryResourceReader;
import org.apache.commons.jci.stores.MemoryResourceStore;
import org.apache.commons.jci.stores.ResourceStore;
import org.apache.commons.jci.stores.ResourceStoreClassLoader;

public class JCIUtil {
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		System.out.println(compile("Test", "public class Test{}").newInstance());
	}

	private static final ThreadLocal<String> errorLocal = new ThreadLocal<String>();
	private static final ThreadLocal<String> warningLocal = new ThreadLocal<String>();
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public static Class<?> compile(String javaClassName, String javaClassContent) {
		MemoryResourceReader src = new MemoryResourceReader();
		MemoryResourceStore dst = new MemoryResourceStore();
		ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
		if (parentClassLoader == null) {
			parentClassLoader = JCIUtil.class.getClassLoader();
		}
		ResourceStoreClassLoader classLoader = new ResourceStoreClassLoader(parentClassLoader, new ResourceStore[] { dst });
		src.add(javaClassName.replace('.', '/') + ".java", javaClassContent.getBytes());
		String[] fileNames = { javaClassName };
		JavaCompiler compiler = new JavaCompilerFactory().createCompiler("Eclipse");
		CompilationResult cr = compiler.compile(fileNames, src, dst, classLoader);
		{
			StringBuilder error = new StringBuilder();
			for (int i = 0; i < cr.getErrors().length; ++i) {
				error.append(cr.getErrors()[i].toString()).append(LINE_SEPARATOR);
			}
			errorLocal.set(error.toString());
		}
		{
			StringBuilder warning = new StringBuilder();
			for (int i = 0; i < cr.getWarnings().length; ++i) {
				warning.append(cr.getWarnings()[i].toString()).append(LINE_SEPARATOR);
			}
			warningLocal.set(warning.toString());
		}
		try {
			return classLoader.loadClass(javaClassName);
		} catch (Exception e) {
			return null;
		}
	}

}

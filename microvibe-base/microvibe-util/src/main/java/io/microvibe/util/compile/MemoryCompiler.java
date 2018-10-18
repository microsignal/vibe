package io.microvibe.util.compile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class MemoryCompiler {

	private static String STRING_URI_SCHEMA_HOST = "";// "string://localhost/";
	private MemoryClassLoader classloader;
	private String classpath;
	private String encoding;

	public static MemoryCompiler newInstance() {
		return new MemoryCompiler();
	}

	public MemoryCompiler() {
		classloader = MemoryClassLoader.newInstance();
		classpath = MemoryClassLoader.getClassPath();
		encoding = System.getProperty("file.encoding");
	}

	public Class<?> compile(final String classname, final String source) throws CompileException {
		return compile(classname, source, null, null);
	}

	public Class<?> compile(final String classname, final String source, final String encoding) throws CompileException {
		return compile(classname, source, encoding, null);
	}

	public Class<?> compile(final String classname, final String source, final String encoding, final String classpath)
			throws CompileException {
		final ByteArrayOutputStream classbytes = new ByteArrayOutputStream();

		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(diagnostics, null, null);

		List<String> options = new ArrayList<String>();
		options.add("-encoding");
		if (encoding != null && !"".equals(encoding)) {
			options.add(encoding);
		} else {
			options.add(this.encoding);
		}
		options.add("-classpath");
		if (classpath != null && !"".equals(classpath)) {
			options.add(classpath);
		} else {
			options.add(this.classpath);
		}

		List<JavaFileObject> jFileObjList = new ArrayList<JavaFileObject>();
		jFileObjList.add(new SimpleJavaFileObject(URI.create(STRING_URI_SCHEMA_HOST + classname.replace('.', '/')
				+ JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE) {
			@Override
			public CharSequence getCharContent(boolean ignoreEncodingErrors) {
				return source;
			}
		});

		JavaCompiler.CompilationTask task = javaCompiler.getTask(null,
				new ForwardingJavaFileManager<StandardJavaFileManager>(fileManager) {
					@Override
					public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling)
							throws IOException {
						return new SimpleJavaFileObject(URI.create(STRING_URI_SCHEMA_HOST + classname.replace('.', '/')
								+ kind.extension), kind) {
							@Override
							public OutputStream openOutputStream() throws IOException {
								return classbytes;
							}
						};
					}

				}, diagnostics, options, null, jFileObjList);

		// 编译源程序
		boolean success = task.call();
		if (success) {
			Class<?> clazz = classloader.defineMemoryClass(classname, classbytes.toByteArray());
			return clazz;
		} else {
			throw new CompileException(compileError(diagnostics));
		}
	}

	private String compileError(DiagnosticCollector<JavaFileObject> diagnostics) {
		StringBuilder sb = new StringBuilder();
		for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
			sb.append(compileError(diagnostic)).append("\n");
		}
		return sb.toString();
	}

	private String compileError(Diagnostic<?> diagnostic) {
		StringBuilder sb = new StringBuilder();
		/*
		sb.append("\tCode:[" + diagnostic.getCode() + "]\n");
		sb.append("\tKind:[" + diagnostic.getKind() + "]\n");
		sb.append("\tPosition:[" + diagnostic.getPosition() + "]\n");
		sb.append("\tStart Position:[" + diagnostic.getStartPosition() + "]\n");
		sb.append("\tEnd Position:[" + diagnostic.getEndPosition() + "]\n");
		sb.append("\tSource:[" + diagnostic.getSource() + "]\n");
		sb.append("\tMessage:[" + diagnostic.getMessage(null) + "]\n");
		sb.append("\tLineNumber:[" + diagnostic.getLineNumber() + "]\n");
		sb.append("\tColumnNumber:[" + diagnostic.getColumnNumber() + "]\n");
		*/
		sb.append("\tLine/Column: ").append(diagnostic.getLineNumber()).append("/").append(diagnostic.getColumnNumber());
		System.err.println(diagnostic.toString());
		System.err.println(sb);
		return diagnostic.toString();
	}
}

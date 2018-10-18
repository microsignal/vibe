package io.microvibe.util.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * @since 1.0 , Java 1.8 , Sep 13, 2016
 * @version 1.0
 * @author Qt
 */
public class IOUtil {
	private static final int EOF = -1;
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	public static void close(final Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
			}
		}
	}

	public static void close(ServerSocket server) {
		if (server != null) {
			try {
				server.close();
			} catch (IOException e) {
			}
		}
	}

	public static void close(final Socket socket) {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}

	public static int copy(final File src, final File dest) throws IOException {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(src);
			out = new FileOutputStream(dest);
			int i = IOUtil.copy(in, out);
			out.flush();
			return i;
		} finally {
			IOUtil.close(out);
			IOUtil.close(in);
		}
	}

	public static int copy(final InputStream input, final OutputStream output) throws IOException {
		long count = IOUtil.copyLarge(input, output);
		if (count > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) count;
	}

	public static void copy(final InputStream input, final Writer output) throws IOException {
		IOUtil.copy(input, output, Charset.defaultCharset());
	}

	public static void copy(final InputStream input, final Writer output, final Charset encoding)
			throws IOException {
		InputStreamReader in = new InputStreamReader(input, encoding);
		IOUtil.copy(in, output);
	}

	public static void copy(final InputStream input, final Writer output, final String encoding)
			throws IOException {
		IOUtil.copy(input, output, encoding);
	}

	public static void copy(final Reader input, final OutputStream output) throws IOException {
		IOUtil.copy(input, output, Charset.defaultCharset());
	}

	public static void copy(final Reader input, final OutputStream output, final Charset encoding)
			throws IOException {
		OutputStreamWriter out = new OutputStreamWriter(output, encoding);
		IOUtil.copy(input, out);
		out.flush();
	}

	public static void copy(final Reader input, final OutputStream output, final String encoding)
			throws IOException {
		IOUtil.copy(input, output, encoding);
	}

	public static int copy(final Reader input, final Writer output) throws IOException {
		long count = IOUtil.copyLarge(input, output);
		if (count > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) count;
	}

	public static long copyLarge(final InputStream input, final OutputStream output)
			throws IOException {
		return IOUtil.copyLarge(input, output, new byte[DEFAULT_BUFFER_SIZE]);
	}

	public static long copyLarge(final InputStream input, final OutputStream output,
			final byte[] buffer) throws IOException {
		long count = 0;
		int n = 0;
		while (EOF != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static long copyLarge(final Reader input, final Writer output) throws IOException {
		return IOUtil.copyLarge(input, output, new char[DEFAULT_BUFFER_SIZE]);
	}

	public static long copyLarge(final Reader input, final Writer output, final char[] buffer)
			throws IOException {
		long count = 0;
		int n = 0;
		while (EOF != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static InputStream getInputStream(final String path) throws FileNotFoundException {
		try {
			StackTraceElement[] traces = new Throwable().getStackTrace();
			StackTraceElement trace = traces.length > 1 ? traces[1] : traces[0];
			Class<?> clazz = Class.forName(trace.getClassName());
			return IOUtil.getInputStream(path, clazz);
		} catch (ClassNotFoundException e) {
			return IOUtil.getInputStream(path, IOUtil.class);
		}
	}

	@SuppressWarnings("resource")
	public static InputStream getInputStream(String path, final Class<?> clazz)
			throws FileNotFoundException {
		InputStream in = null;
		try {
			in = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			if (path.startsWith("/")) {
				path = path.substring(1);
				in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
				if (in == null) {
					in = clazz.getResourceAsStream(path);
				}
			} else {
				in = clazz.getResourceAsStream(path);
				if (in == null) {
					in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
				}
			}
			if (in == null) {
				in = ClassLoader.getSystemResourceAsStream(path);
			}
			if (in == null) {
				throw new FileNotFoundException(path);
			}
		}
		return in;
	}

	private IOUtil() {
	}

}

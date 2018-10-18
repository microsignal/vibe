package io.microvibe.booster.core.base.resource;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

public final class MultiInputStream extends InputStream {

	public static MultiInputStream openResources(Resource... resources) throws IOException {
		return new MultiInputStream(resources);
	}

	public static MultiInputStream open(InputStream... inputStreams) throws IOException {
		return new MultiInputStream(inputStreams);
	}

	public static MultiInputStream openFileSystemResources(String locationPattern) throws IOException {
		return new MultiInputStream(locationPattern);
	}

	public static MultiInputStream openClasspathResources(String locationPattern) throws IOException {
		try {
			StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
			return openClasspathResources(Class.forName(stackTraces[1].getClassName()), locationPattern);
		} catch (ClassNotFoundException e) {
			return openClasspathResources(MultiInputStream.class, locationPattern);
		}
	}

	public static MultiInputStream openClasspathResources(Class<?> clazz, String locationPattern) throws IOException {
		return new MultiInputStream(clazz, locationPattern);
	}

	private Deque<Resource> queue = new ArrayDeque<>();
	private InputStream in;
	private boolean open = false;

	public MultiInputStream() {
	}

	public MultiInputStream(String locationPattern) throws IOException {
		appendFileSystemResource(locationPattern);
	}

	public MultiInputStream(Class<?> clazz, String locationPattern) throws IOException {
		appendClasspathResource(clazz, locationPattern);
	}

	public MultiInputStream(Resource... resources) throws IOException {
		for (Resource resource : resources) {
			queue.offerLast(resource);
		}
	}

	public MultiInputStream(InputStream... inputStreams) {
		for (InputStream inputStream : inputStreams) {
			queue.offerLast(new InputStreamResource(inputStream));
		}
	}

	public MultiInputStream(Iterable<Resource> iterable) throws IOException {
		Iterator<Resource> iter = iterable.iterator();
		while (iter.hasNext()) {
			queue.offerLast(iter.next());
		}
	}

	public MultiInputStream appendClasspathResource(String locationPattern) throws IOException {
		try {
			StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
			return appendClasspathResource(Class.forName(stackTraces[1].getClassName()), locationPattern);
		} catch (ClassNotFoundException e) {
			return appendClasspathResource(MultiInputStream.class, locationPattern);
		}
	}

	public MultiInputStream appendClasspathResource(Class<?> clazz, String locationPattern) throws IOException {
		ResourcePatternResolver resolver = ResourcePatternUtils
				.getResourcePatternResolver(new ClassRelativeResourceLoader(clazz));
		Resource[] resources = resolver.getResources(locationPattern);
		return appendResource(resources);
	}

	public MultiInputStream appendFileSystemResource(String locationPattern) throws IOException {
		ResourcePatternResolver resolver = ResourcePatternUtils
				.getResourcePatternResolver(new FileSystemResourceLoader());
		Resource[] resources = resolver.getResources(locationPattern);
		return appendResource(resources);
	}

	public MultiInputStream appendResource(Resource... resources) {
		for (Resource resource : resources) {
			queue.offerLast(resource);
		}
		return this;
	}

	public MultiInputStream appendInputStream(InputStream... inputStreams) {
		for (InputStream inputStream : inputStreams) {
			queue.offerLast(new InputStreamResource(inputStream));
		}
		return this;
	}

	@Override
	public void close() throws IOException {
		IOUtils.closeQuietly(in);
		in = null;
	}

	private void beOpen() throws IOException {
		if (!open) {
			advance();
			open = true;
		}
	}

	private void advance() throws IOException {
		close();
		while (true) {
			Resource res = queue.pollFirst();
			if (res != null) {
				try {
					in = res.getInputStream();
					String filename = res.getFilename();
					if (filename != null && filename.contains(".gz")) {
						if (in.markSupported()) {
							in.mark(Integer.MAX_VALUE);
						}
						try {
							InputStream gzip = new GZIPInputStream(in);
							in = gzip;
						} catch (IOException e) {
							if (in.markSupported()) {
								in.reset();
							}
						}
					}
					break;
				} catch (FileNotFoundException e) {
					continue;
				}
			} else {
				break;
			}
		}
	}

	@Override
	public int available() throws IOException {
		beOpen();
		if (in == null) {
			return 0;
		}
		return in.available();
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public int read() throws IOException {
		beOpen();
		if (in == null) {
			return -1;
		}
		int result = in.read();
		if (result == -1) {
			advance();
			return read();
		}
		return result;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		beOpen();
		if (in == null) {
			return -1;
		}
		int result = in.read(b, off, len);
		if (result == -1) {
			advance();
			return read(b, off, len);
		}
		return result;
	}

	@Override
	public long skip(long n) throws IOException {
		beOpen();
		if (in == null || n <= 0) {
			return 0;
		}
		long result = in.skip(n);
		if (result != 0) {
			return result;
		}
		if (read() == -1) {
			return 0;
		}
		return 1 + in.skip(n - 1);
	}
}

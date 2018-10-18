package io.microvibe.booster.core.base.web.filter;

import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
//import org.springframework.cglib.proxy.Enhancer;
//import org.springframework.cglib.proxy.MethodInterceptor;
//import org.springframework.cglib.proxy.MethodProxy;

public class HttpServletRequestCacheWrapper extends HttpServletRequestWrapper implements HttpServletRequest {

	private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";
	private static final String METHOD_POST = "POST";
	private final ByteArrayOutputStream requestParametersCache;
	private byte[] contentData;
	private HttpServletRequest request;
	//    private HttpServletRequest requestProxy;
	private boolean cached = false;

	private HttpServletRequestCacheWrapper(HttpServletRequest request) {
		super(request);
		this.request = request;
		int contentLength = request.getContentLength();
		this.requestParametersCache = new ByteArrayOutputStream(contentLength >= 0 ? contentLength : 1024);
		// parse(request);
	}

	public static HttpServletRequest wrap(HttpServletRequest request) {
		if (request instanceof HttpServletRequestCacheWrapper) {
			return (HttpServletRequestCacheWrapper) request;
		}
		return new HttpServletRequestCacheWrapper(request);
	}

    /*private HttpServletRequest createProxy(HttpServletRequest request) {
        return this.requestProxy = (HttpServletRequest) Enhancer.create(request.getClass(), new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                if (method.getName().equals("getInputStream") && method.getParameterCount() == 0) {
                    return HttpServletRequestCacheWrapper.this.getInputStream();
                }
                if (method.getName().equals("getReader") && method.getParameterCount() == 0) {
                    return HttpServletRequestCacheWrapper.this.getReader();
                }
                return proxy.invoke(request, args);
            }
        });
    }*/

	@Override
	public String getParameter(String name) {
		cacheRequestParameters();
		return super.getParameter(name);
//        return requestProxy.getParameter(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		cacheRequestParameters();
		return super.getParameterMap();
//        return requestProxy.getParameterMap();
	}

	@Override
	public Enumeration<String> getParameterNames() {
		cacheRequestParameters();
		return super.getParameterNames();
//        return requestProxy.getParameterNames();
	}

	@Override
	public String[] getParameterValues(String name) {
		cacheRequestParameters();
		return super.getParameterValues(name);
//        return requestProxy.getParameterValues(name);
	}

	private void parse(HttpServletRequest request) {
		if (cached) {
			return;
		}
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ServletInputStream in = request.getInputStream();
			if (in != null) {
				IOUtils.copyLarge(in, bos, new byte[4096]);
				contentData = bos.toByteArray();
			} else {
				contentData = null;
			}
		} catch (IOException e) {
		}
		cached = true;
	}

	public byte[] getContentData() {
		parse(this.request);
		return contentData == null ? requestParametersCache.toByteArray() : contentData;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		parse(this.request);
		if (contentData == null) {
			return null;
		}
		return new ServletInputStream() {
			ByteArrayInputStream bis = new ByteArrayInputStream(contentData);

			public int read() throws IOException {
				return bis.read();
			}

			public boolean isReady() {
				return true;
			}

			public boolean isFinished() {
				return bis.available() == 0;
			}

			@Override
			public void setReadListener(ReadListener listener) {
			}
		};
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
	}

	@Override
	public String getCharacterEncoding() {
		String enc = super.getCharacterEncoding();
		return (enc != null ? enc : "ISO-8859-1");
	}

	private void cacheRequestParameters() {
		if (this.requestParametersCache.size() == 0 && isFormPost()) {
			writeRequestParametersToCachedContent();
		}
	}

	private boolean isFormPost() {
		String contentType = getContentType();
		return (contentType != null && contentType.contains(FORM_CONTENT_TYPE) &&
			METHOD_POST.equalsIgnoreCase(getMethod()));
	}

	private void writeRequestParametersToCachedContent() {
		try {
			if (this.requestParametersCache.size() == 0) {
				String requestEncoding = getCharacterEncoding();
				Map<String, String[]> form = super.getParameterMap();
//                Map<String, String[]> form = requestProxy.getParameterMap();
				for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext(); ) {
					String name = nameIterator.next();
					List<String> values = Arrays.asList(form.get(name));
					for (Iterator<String> valueIterator = values.iterator(); valueIterator.hasNext(); ) {
						String value = valueIterator.next();
						this.requestParametersCache.write(URLEncoder.encode(name, requestEncoding).getBytes());
						if (value != null) {
							this.requestParametersCache.write('=');
							this.requestParametersCache.write(URLEncoder.encode(value, requestEncoding).getBytes());
							if (valueIterator.hasNext()) {
								this.requestParametersCache.write('&');
							}
						}
					}
					if (nameIterator.hasNext()) {
						this.requestParametersCache.write('&');
					}
				}
			}
		} catch (IOException ex) {
			throw new IllegalStateException("Failed to write request parameters to cached content", ex);
		}
	}
}

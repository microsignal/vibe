package io.microvibe.util.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.StatusLine;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.microvibe.util.io.IOUtil;

public class HttpUtil {
	public static final String DEFAULT_CHARSET = "UTF-8";
	static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	static final ThreadLocal<StatusLine> httpStatusLineLocal = new ThreadLocal<StatusLine>();

	/**
	 * 获取当前线程的上一次请求的Http Status Line
	 *
	 * @return
	 */
	public static StatusLine getHttpStatusLine() {
		return httpStatusLineLocal.get();
	}

	public static String get(String uri) throws IOException {
		return get(uri, DEFAULT_CHARSET, null, null);
	}

	public static String get(String uri, String charset) throws IOException {
		return get(uri, charset, null, null);
	}

	public static String get(String uri, Map<String, String> params) throws IOException {
		return get(uri, DEFAULT_CHARSET, params, null);
	}

	public static String get(String uri, String charset, Map<String, String> params) throws IOException {
		return get(uri, charset, params, null);
	}

	public static String get(String uri, Header[] headers) throws IOException {
		return get(uri, DEFAULT_CHARSET, null, headers);
	}

	public static String get(String uri, String charset, Header[] headers) throws IOException {
		return get(uri, charset, null, headers);
	}

	public static String get(String uri, String charset, Map<String, String> params, Header[] headers) throws IOException {
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(uri);
			if (uri.indexOf("?") < 0) {
				sb.append("?");
			} else {
				sb.append("&");
			}
			if (params != null && !params.isEmpty()) {
				Set<Entry<String, String>> entrySet = params.entrySet();
				for (Entry<String, String> entry : entrySet) {
					sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), charset)).append("&");
				}
			}
			if (sb.charAt(sb.length() - 1) == '&' || sb.charAt(sb.length() - 1) == '?') {
				sb.deleteCharAt(sb.length() - 1);
			}
			uri = sb.toString();

			GetMethod method = new GetMethod(uri);
			charset = charset == null ? DEFAULT_CHARSET : charset;
			// postMethod.getParams().setContentCharset(charset);
			method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);
			if (headers != null) {
				for (Header header : headers) {
					method.addRequestHeader(header);
				}
			}
			HttpClient client = new HttpClient();
			client.executeMethod(method);
			httpStatusLineLocal.set(method.getStatusLine());
			/*
			if (HttpStatus.SC_OK != method.getStatusCode()) {
				logger.error("Http状态为: " + method.getStatusLine());
				throw new HttpException("Http状态为: " + method.getStatusLine());
			}
			*/
			InputStream in = method.getResponseBodyAsStream();
			br = new BufferedReader(new InputStreamReader(in, charset));
			StringWriter sw = new StringWriter();
			bw = new BufferedWriter(sw);
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				bw.write(line);
				bw.newLine();
			}
			bw.flush();
			String body = sw.toString();
			// String body = postMethod.getResponseBodyAsString();
			// body = (new String(body.getBytes("ISO8859-1"), "UTF-8"));
			return body;
		} finally {
			IOUtil.close(br);
			IOUtil.close(bw);
		}
	}

	public static String post(String uri) throws IOException {
		return post(uri, DEFAULT_CHARSET, (Map<String, String>) null, (Header[]) null);
	}

	public static String post(String uri, String charset) throws IOException {
		return post(uri, charset, (Map<String, String>) null, (Header[]) null);
	}

	public static String post(String uri, Map<String, String> params) throws IOException {
		return post(uri, DEFAULT_CHARSET, params, (Header[]) null);
	}

	public static String post(String uri, String charset, Map<String, String> params) throws IOException {
		return post(uri, charset, params, (Header[]) null);
	}

	public static String post(String uri, Header[] headers) throws IOException {
		return post(uri, DEFAULT_CHARSET, (Map<String, String>) null, headers);
	}

	public static String post(String uri, String charset, Header[] headers) throws IOException {
		return post(uri, charset, (Map<String, String>) null, headers);
	}

	public static String post(String uri, String charset, Map<String, String> params, Header[] headers)
			throws IOException {
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			PostMethod method = new PostMethod(uri);
			charset = charset == null ? DEFAULT_CHARSET : charset;
			// postMethod.getParams().setContentCharset(charset);
			method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);
			if (params != null && !params.isEmpty()) {
				Set<Entry<String, String>> entrySet = params.entrySet();
				for (Entry<String, String> entry : entrySet) {
					NameValuePair pair = new NameValuePair(entry.getKey(), entry.getValue());
					method.addParameter(pair);
				}
			}

			if (headers != null) {
				for (Header header : headers) {
					method.addRequestHeader(header);
				}
			}
			HttpClient client = new HttpClient();
			client.executeMethod(method);
			httpStatusLineLocal.set(method.getStatusLine());
			/*
			if (HttpStatus.SC_OK != method.getStatusCode()) {
				logger.error("Http状态为: " + method.getStatusLine());
				throw new HttpException("Http状态为: " + method.getStatusLine());
			}
			*/
			InputStream in = method.getResponseBodyAsStream();
			br = new BufferedReader(new InputStreamReader(in, charset));
			StringWriter sw = new StringWriter();
			bw = new BufferedWriter(sw);
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				bw.write(line);
				bw.newLine();
			}
			bw.flush();
			String body = sw.toString();
			// String body = postMethod.getResponseBodyAsString();
			// body = (new String(body.getBytes("ISO8859-1"), "UTF-8"));
			return body;
		} finally {
			IOUtil.close(br);
			IOUtil.close(bw);
		}
	}

	public static String post(String uri, String charset, String requestContent) throws IOException {
		return post(uri, charset, requestContent, null);
	}

	public static String post(String uri, String charset, String requestContent, Header[] headers) throws IOException {
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			PostMethod method = new PostMethod(uri);
			charset = charset == null ? DEFAULT_CHARSET : charset;
			// postMethod.getParams().setContentCharset(charset);
			method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);

			if (headers != null) {
				for (Header header : headers) {
					method.addRequestHeader(header);
				}
			}

			method.setRequestEntity(new StringRequestEntity(requestContent, "text/html", charset));

			HttpClient client = new HttpClient();
			client.executeMethod(method);
			httpStatusLineLocal.set(method.getStatusLine());
			/*
			if (HttpStatus.SC_OK != method.getStatusCode()) {
				logger.error("Http状态为: " + method.getStatusLine());
				throw new HttpException("Http状态为: " + method.getStatusLine());
			}
			*/
			InputStream in = method.getResponseBodyAsStream();
			br = new BufferedReader(new InputStreamReader(in, charset));
			StringWriter sw = new StringWriter();
			bw = new BufferedWriter(sw);
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				bw.write(line);
				bw.newLine();
			}
			bw.flush();
			String body = sw.toString();
			// String body = postMethod.getResponseBodyAsString();
			// body = (new String(body.getBytes("ISO8859-1"), "UTF-8"));
			return body;
		} finally {
			IOUtil.close(br);
			IOUtil.close(bw);
		}
	}
	/*
	GetMethod getMethod = new GetMethod("http://www.baidu.com");
	//（1）、这里可以设置自己想要的编码格式
	getMethod.getParams().setContentCharset("GB2312");

	//（2）、对于get方法也可以这样设置
	getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"GB2312");

	//（3）、还可以如下这样设置
	getMethod.addRequestHeader("Content-Type", "text/html; charset=UTF-8");

	//（4）、当然同样可以直接设置 httpClient 对象的编码格式
	HttpClient httpClient = new HttpClient();
	httpClient.getParams().setContentCharset("GB2312");

	//使用流的方式读取也可以如下设置
	InputStream in = getMethod.getResponseBodyAsStream();
	//这里的编码规则要与上面的相对应
	BufferedReader br = new BufferedReader(new InputStreamReader(in,"GB2312"));
	*/
	/*
	PostMethod PostMethod= new PostMethod("http://localhost:8080/ezid-cert-mobile/upload");
	//（1）、通常可以如下设置自己想要的编码格式
	postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"utf-8");

	//（2）、也重载PostMethod的getRequestCharSet()方法
	public  class UTF8PostMethod extends PostMethod{
	    public UTF8PostMethod(String uri){
	        super(uri);
	    }
	    @Override
	    public String getRequestCharSet() {
	     return "UTF-8";
	    }
	}

	//（3）、如果是方法的参数出现乱码问题，那么你可以如下设置参数
	Charset utf8Charset = Charset.forName("UTF-8");
	multipartContent.addPart("name", new StringBody(Info.getUserEntity().getName(), utf8Charset));

	//（4）、如果你用的是Part [] parts={...}传参方式的话可以如下设置
	StringPart name=new StringPart("name",certFormEntity.getPersonName(), "UTF-8");
	 */
}

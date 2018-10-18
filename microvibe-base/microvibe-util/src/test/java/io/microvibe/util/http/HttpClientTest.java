package io.microvibe.util.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.microvibe.util.io.IOUtil;
import lombok.Cleanup;

public class HttpClientTest {

	public static final String DEFAULT_CHARSET = "UTF-8";
	static final Logger logger = LoggerFactory.getLogger(HttpClientTest.class);
	static final ThreadLocal<StatusLine> httpStatusLineLocal = new ThreadLocal<StatusLine>();

	public static String get(String uri, String charset, Map<String, String> params, Header[] headers)
			throws IOException {
		@Cleanup
		BufferedReader br = null;
		@Cleanup
		BufferedWriter bw = null;
		@Cleanup
		CloseableHttpClient client = null;
		// client = HttpClients.createDefault();
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

			HttpGet httpGet = new HttpGet(uri);

			charset = charset == null ? DEFAULT_CHARSET : charset;
			RequestConfig rc = RequestConfig.custom()
					.build();
			httpGet.setConfig(rc);
			if (headers != null) {
				for (Header header : headers) {
					httpGet.addHeader(header);
				}
			}

			ConnectionConfig config = ConnectionConfig.custom()
					.setCharset(Charset.forName(charset))
					.build();
			client = HttpClients.custom()
					.setDefaultConnectionConfig(config)
					.build();
			CloseableHttpResponse response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			StatusLine statusLine = response.getStatusLine();
			httpStatusLineLocal.set(statusLine);

			InputStream in = entity.getContent();
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
			IOUtil.close(client);
		}
	}

	public static void main(String[] args) throws IOException {
		Map<String, String> param = new HashMap<>();
		param.put("id", "123");
		Header[] headers = new Header[] {
				new BasicHeader("test", "test")
		};
//		String rs = get("http://localhost:8080/test", "utf-8", param, headers);
		String rs = get("https://www.baidu.com/", "utf-8", param, headers);
		System.out.println(rs);
	}
}

package io.microvibe.booster.core.base.web.filter;

import io.microvibe.booster.commons.crypto.MessageDigestUtil;
import io.microvibe.booster.commons.err.ResubmitException;
import io.microvibe.booster.commons.schedule.Schedules;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * 重复提交校验器
 *
 * @author Qt
 * @version 1.0.1
 * @since Dec 13, 2017
 */
@Slf4j
public class ResubmitFilter extends OncePerRequestFilter {
	private long minInterval = 500L;// 最小重复提交间隔
	private Map<String, Long> cache;// hash缓存
	private Deque<String> queue = new LinkedBlockingDeque<>();// 清理队列
	// private Timer cleaningTimer = new Timer(true);// 清理定时器
	private boolean enabled = true;// 是否启用过滤器
	private boolean checkingBodyStream = true;

	private String methods = "POST,PUT,DELETE_BY_ID,PATCH";// 需要过滤的请求方法
	private Set<String> requiredMethodSet = new HashSet<>();
	private boolean methodCheckingRequired;

	private String uriList;// 需要过滤的uri
	private Set<String> requiredURISet = new HashSet<>();
	private boolean uriCheckingRequired;

	private String uriPatterns;// 需要过滤的uri
	private Set<Pattern> requiredURIPatternSet = new HashSet<>();
	private boolean uriPatternCheckingRequired;

	private String whiteList;// 不需要过滤的uri
	private Set<String> requiredWhiteSet = new HashSet<>();
	private boolean whiteCheckingRequired;
	private String whitePatterns;// 不需要过滤的uri
	private Set<Pattern> requiredWhitePatternSet = new HashSet<>();
	private boolean whitePatternCheckingRequired;

	public ResubmitFilter() {
		cache = Collections.synchronizedMap(new WeakHashMap<>());
	}

	@Override
	protected void initFilterBean() throws ServletException {
		if (methods != null) {
			String[] arr = methods.split("[\\s,;|]+");
			for (String s : arr) {
				if ((s = s.trim().toUpperCase()).length() > 0) {
					requiredMethodSet.add(s);
				}
			}
		}
		methodCheckingRequired = requiredMethodSet != null && requiredMethodSet.size() > 0;

		if (uriList != null) {
			String[] arr = uriList.split("[\\s,;|]+");
			for (String s : arr) {
				if ((s = s.trim()).length() > 0) {
					requiredURISet.add(s);
				}
			}
		}
		uriCheckingRequired = requiredURISet != null && requiredURISet.size() > 0;

		if (uriPatterns != null) {
			String[] arr = uriPatterns.split("[\\s,;|]+");
			for (String s : arr) {
				if ((s = s.trim()).length() > 0) {
					requiredURIPatternSet.add(Pattern.compile(s));
				}
			}
		}
		uriPatternCheckingRequired = requiredURIPatternSet != null && requiredURIPatternSet.size() > 0;

		if (whiteList != null) {
			String[] arr = whiteList.split("[\\s,;|]+");
			for (String s : arr) {
				if ((s = s.trim()).length() > 0) {
					requiredWhiteSet.add(s);
				}
			}
		}
		whiteCheckingRequired = requiredWhiteSet != null && requiredWhiteSet.size() > 0;

		if (whitePatterns != null) {
			String[] arr = whitePatterns.split("[\\s,;|]+");
			for (String s : arr) {
				if ((s = s.trim()).length() > 0) {
					requiredWhitePatternSet.add(Pattern.compile(s));
				}
			}
		}
		whitePatternCheckingRequired = requiredWhitePatternSet != null && requiredWhitePatternSet.size() > 0;

		// timer
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Deque<String> queue = ResubmitFilter.this.queue;
				long threshold = System.currentTimeMillis() - minInterval;
				int queueSize = queue.size();
				int expected = queueSize >>> 1;// clean: 1/2
				int cleaned = 0;
				log.debug("[before]queue-size: {}, cache-size: {}", queueSize, cache.size());
				for (Iterator<String> iter = queue.iterator(); iter.hasNext(); ) {
					String s = iter.next();
					Long mills = cache.get(s);
					cleaned++;
					if (mills != null && mills >= threshold) {
						if (cleaned > expected) {
							break;
						} else {
							continue;
						}
					}
					cache.remove(s);
					iter.remove();
				}
				log.debug("[after]queue-size: {}, cache-size: {}", queue.size(), cache.size());
			}
		};
		// cleaningTimer.schedule(task, minInterval, minInterval);
		Schedules.executor().scheduleAtFixedRate(task, minInterval, minInterval, TimeUnit.MILLISECONDS);
	}

	boolean isCheckingRequired(HttpServletRequest request) {
		if (!enabled) {
			return false;
		}
		if (methodCheckingRequired) {
			if (!requiredMethodSet.contains(request.getMethod().toUpperCase())) {
				return false;
			}
		}
		String requiredURI = request.getRequestURI().substring(request.getContextPath().length());
		PathMatcher pathMatcher = new AntPathMatcher();
		if (whiteCheckingRequired) {
			for (String whiteUri : requiredWhiteSet) {
				if (pathMatcher.match(whiteUri, requiredURI)) {
					return false;
				}
			}
		}
		if (whitePatternCheckingRequired) {
			for (Pattern pattern : requiredWhitePatternSet) {
				if (pattern.matcher(requiredURI).matches()) {
					return false;
				}
			}
		}
		boolean rs = !(uriCheckingRequired || uriPatternCheckingRequired);
		if (uriCheckingRequired) {
			for (String uri : requiredURISet) {
				if (pathMatcher.match(uri, requiredURI)) {
					rs = true;
					break;
				}
			}
		}
		if (uriPatternCheckingRequired) {
			for (Pattern pattern : requiredURIPatternSet) {
				if (pattern.matcher(requiredURI).matches()) {
					rs = true;
					break;
				}
			}
		}
		return rs;
	}

	void check(HttpServletRequest request) {
		String sha1 = sha1(request);
		long millis = System.currentTimeMillis();
		log.info("request-sha1: {}, time: {}", sha1, millis);
		Long lastMills = cache.get(sha1);
		if (lastMills != null && lastMills.longValue() + minInterval >= millis) {
			log.info("request-sha1: {}, time: {}, last: {}", sha1, millis, lastMills);
			throw new ResubmitException();
		}
		cache(sha1, millis);
	}

	void cache(String sha1, long millis) {
		cache.put(sha1, millis);
		queue.offerLast(sha1);
	}

	String sha1(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append("SessionId=").append(request.getRequestedSessionId()).append("\n");
		sb.append("RequestURI=").append(request.getRequestURI()).append("\n");
		sb.append("Method=").append(request.getMethod()).append("\n");
		sb.append("Referer=").append(request.getHeader("Referer")).append("\n");
		sb.append("User-Agent=").append(request.getHeader("User-Agent")).append("\n");

		/*
		 * Enumeration<String> es = request.getHeaderNames();
		 * while(es.hasMoreElements()) { String header = es.nextElement(); String val =
		 * request.getHeader(header); }
		 */

		Map<String, String[]> parameterMap = request.getParameterMap();
		Iterator<Entry<String, String[]>> iter = parameterMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String[]> entry = iter.next();
			String[] vals = entry.getValue();
			for (int i = 0; i < vals.length; i++) {
				sb.append(entry.getKey()).append("=").append(vals[i]).append("\n");
			}
		}
		String str = sb.toString();
		log.info("request-str: {}", str);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			bos.write(str.getBytes());
			if (request instanceof HttpServletRequestCacheWrapper) {
				bos.write(((HttpServletRequestCacheWrapper) request).getContentData());
			}
		} catch (IOException e) {
		}
		String sha1 = MessageDigestUtil.sha1AsString(bos.toByteArray());
		return sha1;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		if (isCheckingRequired(request) && !ServletFileUpload.isMultipartContent(request)/*文件上传请求不拦截*/) {
			if (checkingBodyStream) {
				request = HttpServletRequestCacheWrapper.wrap(request);
			}
			check(request);
		}
		filterChain.doFilter(request, response);
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setCheckingBodyStream(boolean checkingBodyStream) {
		this.checkingBodyStream = checkingBodyStream;
	}

	public void setMinInterval(long minInterval) {
		this.minInterval = minInterval;
	}

	public void setMethods(String methods) {
		this.methods = methods;
	}

	public void setUriList(String uriList) {
		this.uriList = uriList;
	}

	public void setUriPatterns(String uriPatterns) {
		this.uriPatterns = uriPatterns;
	}

	public void setWhiteList(String whiteList) {
		this.whiteList = whiteList;
	}

	public void setWhitePatterns(String whitePatterns) {
		this.whitePatterns = whitePatterns;
	}

}

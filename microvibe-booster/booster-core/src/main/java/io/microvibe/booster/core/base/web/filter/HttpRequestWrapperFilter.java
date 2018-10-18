package io.microvibe.booster.core.base.web.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class HttpRequestWrapperFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		// 文件上传请求不作包装
		if (!ServletFileUpload.isMultipartContent(request)) {
			request = HttpServletRequestCacheWrapper.wrap(request);
		}
		filterChain.doFilter(request, response);
	}
}

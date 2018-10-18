package io.microvibe.booster.core.base.web.captcha;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.imageio.ImageIO;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 生成验证码
 */
public class JCaptchaFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		response.setDateHeader("Expires", 0L);
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
		response.setContentType("image/jpeg");

		// 从参数获取
		String id = StringUtils.trimToNull(request.getParameter("id"));
//		if (id == null) {
//			// 从会话获取
//			id = request.getRequestedSessionId();
//		}
		if (id == null) {
			// 未创建会话时, 此时强制建立会话, 否则生成的验证码将没有意义
			id = request.getSession(true).getId();
		}
		BufferedImage bi = JCaptcha.generateForId(id);

		ServletOutputStream out = response.getOutputStream();

		ImageIO.write(bi, "jpg", out);
		try {
			out.flush();
		} finally {
			out.close();
		}
	}

}

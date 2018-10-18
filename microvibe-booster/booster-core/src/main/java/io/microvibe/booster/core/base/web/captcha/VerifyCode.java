package io.microvibe.booster.core.base.web.captcha;

import io.microvibe.booster.core.base.utils.RequestContextUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.function.Consumer;

@Slf4j
public class VerifyCode {

	public static void output(HttpServletResponse response){

		output(response, verifyCode -> {
			HttpServletRequest request = RequestContextUtils.currentHttpRequest();
			//存入会话session
			HttpSession session = request.getSession(true);
			session.setAttribute("captcha", verifyCode.toLowerCase());
		});
	}

	public static void output(HttpServletResponse response,Consumer<String> callback){
		try {
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setContentType("image/jpg");

			//生成随机字串
			String verifyCode = VerifyCodeUtils.generateVerifyCode(4);

			callback.accept(verifyCode);
			//生成图片
			int w = 146, h = 33;
			VerifyCodeUtils.outputImage(w, h, response.getOutputStream(), verifyCode);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
}

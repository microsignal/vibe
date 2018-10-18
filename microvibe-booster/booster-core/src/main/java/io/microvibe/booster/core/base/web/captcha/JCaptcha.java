package io.microvibe.booster.core.base.web.captcha;

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;

@Slf4j
public class JCaptcha {
	public static final EsManageableImageCaptchaService captchaService = new EsManageableImageCaptchaService(
		new FastHashMapCaptchaStore(), new GMailEngine(), 180, 100000, 75000);

	/**
	 * 生成验证码
	 *
	 * @param binded 绑定ID
	 * @return
	 */
	public static BufferedImage generateForId(String binded) {
		return captchaService.getImageChallengeForID(binded);
	}

	/**
	 * 校验验证码
	 *
	 * @param binded 绑定ID
	 * @param input
	 * @return
	 */
	public static boolean validateForId(String binded, String input) {
		boolean validated = false;
		try {
			validated = captchaService.validateResponseForID(binded, input).booleanValue();
		} catch (CaptchaServiceException e) {
			log.trace(e.getMessage(), e);
		}
		return validated;
	}

	/**
	 * 校验与会话绑定的验证码,不可重复校验
	 *
	 * @param request
	 * @param userCaptchaResponse
	 * @return
	 */
	public static boolean validateResponse(HttpServletRequest request, String userCaptchaResponse) {
		if (request.getSession(false) == null) {
			return false;
		}
		boolean validated = false;
		try {
			String id = request.getSession().getId();
			validated = captchaService.validateResponseForID(id, userCaptchaResponse).booleanValue();
		} catch (CaptchaServiceException e) {
			log.trace(e.getMessage(), e);
		}
		return validated;
	}

	/**
	 * 校验与会话绑定的验证码,可重复校验
	 *
	 * @param request
	 * @param userCaptchaResponse
	 * @return
	 */
	public static boolean hasCaptcha(HttpServletRequest request, String userCaptchaResponse) {
		if (request.getSession(false) == null) {
			return false;
		}
		boolean validated = false;
		try {
			String id = request.getSession().getId();
			validated = captchaService.hasCapcha(id, userCaptchaResponse);
		} catch (CaptchaServiceException e) {
			log.trace(e.getMessage(), e);
		}
		return validated;
	}

}

package io.microvibe.booster.core.base.web.captcha;

import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;

/**
 * @author Qt
 */
public class EsManageableImageCaptchaService extends DefaultManageableImageCaptchaService {

	public EsManageableImageCaptchaService(com.octo.captcha.service.captchastore.CaptchaStore captchaStore, com.octo.captcha.engine.CaptchaEngine captchaEngine, int minGuarantedStorageDelayInSeconds, int maxCaptchaStoreSize, int captchaStoreLoadBeforeGarbageCollection) {
		super(captchaStore, captchaEngine, minGuarantedStorageDelayInSeconds, maxCaptchaStoreSize, captchaStoreLoadBeforeGarbageCollection);
	}

	public boolean hasCapcha(String id, String userCaptchaResponse) {
		return store.getCaptcha(id).validateResponse(userCaptchaResponse);
	}
}

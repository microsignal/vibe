package io.microvibe.booster.core.base.web.captcha;

import java.io.File;
import java.io.IOException;

import static io.microvibe.booster.core.base.web.captcha.VerifyCodeUtils.generateVerifyCode;
import static io.microvibe.booster.core.base.web.captcha.VerifyCodeUtils.outputImage;

/**
 * @author Qt
 * @since May 28, 2018
 */
public class VerifyCodeUtilsTest {

	public static void main(String[] args) throws IOException {
		File dir = new File("D:/data/captcha");
		int w = 200, h = 80;
		for (int i = 0; i < 1; i++) {
			String verifyCode = generateVerifyCode(4);
			File file = new File(dir, verifyCode + ".jpg");
			outputImage(w, h, file, verifyCode);
		}
	}
}

package io.microvibe.booster.commons.utils;

import io.microvibe.booster.commons.spring.MessageResources;
import org.springframework.context.MessageSource;

/**
 * @author Qt
 */
public class MessageUtils {

	private static MessageSource messageSource;

	/**
	 * 根据消息键和参数 获取消息
	 * 委托给spring messageSource
	 *
	 * @param code 消息键
	 * @param args 参数
	 * @return
	 */
	public static String message(String code, Object... args) {
		return MessageResources.getMessage(code, args);
	}

}

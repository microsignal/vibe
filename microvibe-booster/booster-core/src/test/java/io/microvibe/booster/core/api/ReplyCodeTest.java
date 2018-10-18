package io.microvibe.booster.core.api;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.core.schedule.ScheduledClusterAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;

/**
 * @author Qt
 * @since May 28, 2018
 */
public class ReplyCodeTest {

	public static void main(String[] args) {
		System.out.println("响应代码\t响应消息");
		for (ReplyCode rc : ReplyCode.values()) {
			System.out.println(rc.getCode() + "\t" + rc.getMessage());
		}
		System.out.printf("CodeAsInt: %d%n", ReplyCode.Success.getCodeAsInt());
	}
}

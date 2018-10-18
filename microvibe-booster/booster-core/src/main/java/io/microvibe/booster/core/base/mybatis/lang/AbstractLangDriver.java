package io.microvibe.booster.core.base.mybatis.lang;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.core.base.mybatis.configuration.EntityPersistentRecognizerScanner;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

public abstract class AbstractLangDriver extends XMLLanguageDriver {

	private static boolean entityScanned = false;

	public AbstractLangDriver() {
		if (!entityScanned) {
			// 先扫描所有实体类
			ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
			if (applicationContext != null) {
				try {
					applicationContext.getBean(EntityPersistentRecognizerScanner.class);
				} catch (BeansException e) {
					e.printStackTrace();
				}
				entityScanned = true;
			}
		}
	}

}

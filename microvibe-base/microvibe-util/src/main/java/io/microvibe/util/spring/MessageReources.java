package io.microvibe.util.spring;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

//import org.springframework.context.support.MessageSourceResourceBundle;

public class MessageReources {
	private static String messageResourceBase = System.getProperty("messageResourceBase",
			"config.msg;msg");
	private static ResourceBundleMessageSource messageSource;

	private static ResourceBundleMessageSource messageSource() {
		if (MessageReources.messageSource == null) {
			synchronized (MessageReources.class) {
				if (MessageReources.messageSource == null) {
					String[] messageResourceBaseArray = MessageReources.messageResourceBase
							.split("[,;|]+");
					Set<String> basenames = new HashSet<String>();
					for (String messageResourceBase : messageResourceBaseArray) {
						String msgResPath = messageResourceBase
								.replaceFirst("^[/\\.]?", "/").replaceFirst("[/\\.]?$", "/")
								.replaceAll("\\.", "/");
						String resPath = "classpath*:" + msgResPath + "*";
						try {
							ResourcePatternResolver resolver = ResourcePatternUtils
									.getResourcePatternResolver(new ClassRelativeResourceLoader(
											MessageReources.class));
							Pattern[] patterns = new Pattern[]{
									Pattern.compile(msgResPath
											+ "(\\w+)(_\\w+)(_\\w+)\\.(properties|class)$"),
									Pattern.compile(msgResPath
											+ "(\\w+)(_\\w+)\\.(properties|class)$"),
									Pattern.compile(msgResPath + "(\\w+)\\.(properties|class)$")
							};
							Resource[] resources = resolver.getResources(resPath);
							for (Resource resource : resources) {
								String path = resource.getURI().getPath();
								if (path == null) {
									path = resource.getURI().toString();
								}
								String basename = null;
								for (Pattern pattern : patterns) {
									if (basename != null) {
										break;
									}
									Matcher matcher = pattern.matcher(path);
									if (matcher.find()) {
										basename = matcher.group(1);
									}
								}
								if (basename != null) {
									basenames.add(messageResourceBase + "." + basename);
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					MessageReources.messageSource = new ResourceBundleMessageSource();
					MessageReources.messageSource.setBasenames(basenames
							.toArray(new String[basenames.size()]));
					MessageReources.messageSource.setUseCodeAsDefaultMessage(true);
				}
			}
		}
		return MessageReources.messageSource;
	}

	public static void resetMessageResourceBase(final String messageResourceBase) {
		MessageReources.messageResourceBase = messageResourceBase;
		MessageReources.messageSource = null;
	}

	public static String getMessage(final String code, final String defaultDesc,
			final Locale locale, final Object... params) {
		if (ApplicationContextHolder.getApplicationContext() != null) {
			return ApplicationContextHolder.getMessage(code, params, defaultDesc, locale);
		}
		return MessageReources.messageSource().getMessage(code, params, defaultDesc, locale);
	}

	public static String getMessage(final String code, final Object... params) {
		return MessageReources.getMessage(code, code, Locale.getDefault(), params);
	}

}

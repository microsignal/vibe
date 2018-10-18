package io.microvibe.booster.commons.spring;

import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// import org.springframework.context.support.MessageSourceResourceBundle;

public class MessageResources {
	public static final String DEFAULT_MESSAGE_RESOURCE_BASES =
		"config.message;message;config.messages;messages;config.msg;msg;ValidationMessages";
	private static String messageResourceBase = System.getProperty("messageResourceBase", DEFAULT_MESSAGE_RESOURCE_BASES);
	private static ResourceBundleMessageSource messageSource;

	private static ResourceBundleMessageSource messageSource() {
		if (MessageResources.messageSource == null) {
			synchronized (MessageResources.class) {
				if (MessageResources.messageSource == null) {
					String[] messageResourceBaseArray = MessageResources.messageResourceBase.split("[,;|]+");
					Set<String> basenames = new HashSet<String>();
					for (String messageResourceBase : messageResourceBaseArray) {
						try {
							String msgResPath = messageResourceBase.replaceFirst("^[/\\.]?", "")
								.replaceFirst("[/\\.]?$", "").replaceAll("\\.", "/");
							ResourcePatternResolver resolver = ResourcePatternUtils
								.getResourcePatternResolver(new ClassRelativeResourceLoader(MessageResources.class));
							{//basename
								String resPath = "classpath*:/" + msgResPath + "*";
								Pattern[] patterns = new Pattern[]{
									Pattern.compile(msgResPath + "(_[\\w\\-]+)(_[\\w\\-]+)\\.(properties|class)$"),
									Pattern.compile(msgResPath + "(_[\\w\\-]+)\\.(properties|class)$"),
									Pattern.compile(msgResPath + "\\.(properties|class)$")};
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
											basename = messageResourceBase;
										}
									}
									if (basename != null) {
										basenames.add(basename);
									}
								}
							}
							{//subbasename
								String resPath = "classpath*:/" + msgResPath + "/*";
								Pattern[] patterns = new Pattern[]{
									Pattern.compile("([\\w\\-]+)(_[\\w\\-]+)(_[\\w\\-]+)\\.(properties|class)$"),
									Pattern.compile("([\\w\\-]+)(_[\\w\\-]+)\\.(properties|class)$"),
									Pattern.compile("([\\w\\-]+)\\.(properties|class)$")};
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
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					MessageResources.messageSource = new ResourceBundleMessageSource();
					MessageResources.messageSource.setBasenames(basenames.toArray(new String[basenames.size()]));
					MessageResources.messageSource.setUseCodeAsDefaultMessage(true);
				}
			}
		}
		return MessageResources.messageSource;
	}

	public static void resetMessageResourceBase(final String messageResourceBase) {
		MessageResources.messageResourceBase = messageResourceBase;
		MessageResources.messageSource = null;
	}

	public static String getMessage(final String code, final String defaultDesc, final Locale locale,
		final Object... params) {
		if (ApplicationContextHolder.hasApplicationContext()) {
			return ApplicationContextHolder.getMessage(code, params, defaultDesc, locale);
		}
		return MessageResources.messageSource().getMessage(code, params, defaultDesc, locale);
	}

	public static String getMessage(final String code, final Object... params) {
		return MessageResources.getMessage(code, code, Locale.getDefault(), params);
	}

	public static String getMessageOrDefault(final String code, final String defaultDesc, final Object... params) {
		return MessageResources.getMessage(code, defaultDesc, Locale.getDefault(), params);
	}

}

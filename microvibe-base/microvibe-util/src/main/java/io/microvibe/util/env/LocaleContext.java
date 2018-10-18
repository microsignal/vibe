package io.microvibe.util.env;

import java.util.Locale;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LocaleContext {

	static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	static final ThreadLocal<Locale> local = new ThreadLocal<Locale>();

	public static Locale getCurrentLocale() {
		Locale locale = local.get();
		if (local == null) {
			locale = Locale.getDefault();
			local.set(locale);
		}
		return locale;
	}

	public static void setCurrentLocale(Locale locale) {
		local.set(locale);
	}

	public static void setCurrentLocale(String language, String country, String variant) {
		setCurrentLocale(new Locale(language, country, variant));
	}

	public static void setCurrentLocale(String language, String country) {
		setCurrentLocale(new Locale(language, country, ""));
	}

	public static void setCurrentLocale(String language) {
		setCurrentLocale(new Locale(language, "", ""));
	}
}

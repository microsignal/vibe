package io.microvibe.util.spring;

public class ApplicationContextLoaders {

	private static volatile ApplicationContextLoader instance;

	public static ApplicationContextLoader getSingletonContextLoader() {
		if (instance == null) {
			synchronized (ApplicationContextLoader.class) {
				if (instance == null) {
					instance = ApplicationContextLoader.newInstance();
				}
			}
		}
		return instance;
	}

}

package io.microvibe.booster.core.base.datasource;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class EntityManagerFactoryContextHolder {

	private static final ThreadLocal<String> contextHolder = ThreadLocal.withInitial(() -> TargetDataSource.DEFAULT);
	private static Set<String> ids = new ConcurrentSkipListSet<>();

	public static void add(String id) {
		ids.add(id);
	}

	public static String getEntityManagerFactoryType() {
		return contextHolder.get();
	}

	public static void setEntityManagerFactoryType(String type) {
		contextHolder.set(type);
	}

	public static void clearEntityManagerFactoryType() {
		contextHolder.remove();
	}

}

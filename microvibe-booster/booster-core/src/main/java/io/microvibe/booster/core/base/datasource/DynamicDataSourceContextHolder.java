package io.microvibe.booster.core.base.datasource;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class DynamicDataSourceContextHolder {

	private static final ThreadLocal<String> contextHolder = ThreadLocal.withInitial(() -> TargetDataSource.DEFAULT);
	private static Set<String> ids = new ConcurrentSkipListSet<>();

	public static void add(String dataSourceId){
		ids.add(dataSourceId);
	}
	public static String getDataSourceType() {
		return contextHolder.get();
	}

	public static void setDataSourceType(String dataSourceType) {
		contextHolder.set(dataSourceType);
		EntityManagerFactoryContextHolder.setEntityManagerFactoryType(dataSourceType);
	}

	public static void clearDataSourceType() {
		contextHolder.remove();
		EntityManagerFactoryContextHolder.clearEntityManagerFactoryType();
	}

	public static boolean containsDataSource(String dataSourceId) {
		return ids.contains(dataSourceId);
	}
}

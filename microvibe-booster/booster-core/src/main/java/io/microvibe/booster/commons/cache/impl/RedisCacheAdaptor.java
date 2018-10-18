package io.microvibe.booster.commons.cache.impl;


import io.microvibe.booster.commons.cache.RedisNativeCache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public abstract class RedisCacheAdaptor implements RedisNativeCache {

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Object getNativeCache() {
		return null;
	}

	@Override
	public ValueWrapper get(Object key) {
		return null;
	}

	@Override
	public <T> T get(Object key, Class<T> type) {
		return null;
	}

	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		return null;
	}

	@Override
	public void put(Object key, Object value) {
	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		return null;
	}

	@Override
	public void evict(Object key) {
	}

	@Override
	public void clear() {
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Set<Object> keys() {
		return null;
	}

	@Override
	public Map<Object, Object> toMap() {
		return null;
	}

	@Override
	public Collection<Object> values() {
		return null;
	}

}

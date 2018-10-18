package io.microvibe.booster.commons.cache.impl;

import io.microvibe.booster.commons.cache.RedisNativeCache;
import org.springframework.cache.Cache;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class RedisNoopCache extends RedisCacheAdaptor implements RedisNativeCache, Cache {

	private String name;

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object getNativeCache() {
		return this;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Set<Object> keys() {
		return Collections.emptySet();
	}

	@Override
	public Map<Object, Object> toMap() {
		return Collections.emptyMap();
	}

	@Override
	public Collection<Object> values() {
		return Collections.emptySet();
	}

}

package io.microvibe.booster.commons.cache;

import org.springframework.cache.Cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface RedisNativeCache extends Cache {

	int size();

	Set<Object> keys();

	Map<Object, Object> toMap();

	Collection<Object> values();

}

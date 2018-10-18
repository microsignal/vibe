package io.microvibe.booster.commons.cache;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.*;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisNativeCacheWrapper implements RedisNativeCache {

	private final Map<Object, Object> cache = Collections.synchronizedMap(new WeakHashMap<>());
	private final RedisNativeCache proxy;
	private final boolean memcacheable;

	public static RedisNativeCache wrap(RedisNativeCache proxy) {
		return new RedisNativeCacheWrapper(proxy, true);
	}

	public static RedisNativeCache wrap(RedisNativeCache proxy, boolean memcacheable) {
		return new RedisNativeCacheWrapper(proxy, memcacheable);
	}

	@Override
	public int size() {
		try {
			return proxy.size();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if (memcacheable) {
				return cache.size();
			}
			return 0;
		}
	}

	@Override
	public Set<Object> keys() {
		try {
			return proxy.keys();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if (memcacheable) {
				return cache.keySet();
			}
			return Collections.emptySet();
		}
	}

	@Override
	public Map<Object, Object> toMap() {
		try {
			return proxy.toMap();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if (memcacheable) {
				return new HashMap<>(cache);
			}
			return Collections.emptyMap();
		}
	}

	@Override
	public Collection<Object> values() {
		try {
			return proxy.values();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if (memcacheable) {
				return cache.values();
			}
			return Collections.emptySet();
		}
	}

	@Override
	public String getName() {
		try {
			return proxy.getName();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public Object getNativeCache() {
		try {
			return proxy.getNativeCache();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if (memcacheable) {
				return cache;
			}
			return null;
		}
	}

	@Override
	public ValueWrapper get(Object key) {
		try {
			return proxy.get(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if (memcacheable) {
				if (cache.containsKey(key)) {
					return new SimpleValueWrapper(cache.get(key));
				}
			}
			return null;
		}
	}

	@Override
	public <T> T get(Object key, Class<T> type) {
		try {
			return proxy.get(key, type);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if (memcacheable) {
				if (cache.containsKey(key)) {
					return (T) cache.get(key);
				}
			}
			return null;
		}
	}

	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		try {
			return proxy.get(key, valueLoader);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if (memcacheable) {
				if (cache.containsKey(key)) {
					return (T) cache.get(key);
				} else {
					try {
						return valueLoader.call();
					} catch (Exception e1) {
					}
				}
			}
			return null;
		}
	}

	@Override
	public void put(Object key, Object value) {
		{
			try {
				proxy.put(key, value);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				if (memcacheable) {
					cache.put(key, value);
				}
			}
		}

	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		try {
			return proxy.putIfAbsent(key, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if (memcacheable) {
				cache.putIfAbsent(key, value);
			}
			return null;
		}
	}

	@Override
	public void evict(Object key) {
		{
			try {
				proxy.evict(key);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				if (memcacheable) {
					cache.remove(key);
				}
			}
		}

	}

	@Override
	public void clear() {
		try {
			proxy.clear();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if (memcacheable) {
				cache.clear();
			}
		}
	}
}

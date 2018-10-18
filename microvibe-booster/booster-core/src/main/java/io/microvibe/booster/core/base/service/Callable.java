package io.microvibe.booster.core.base.service;

import javax.persistence.EntityManager;

public interface Callable<T> {
	T call(EntityManager entityManager);
}

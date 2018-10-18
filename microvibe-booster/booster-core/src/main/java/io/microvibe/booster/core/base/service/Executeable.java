package io.microvibe.booster.core.base.service;

import javax.persistence.EntityManager;

public interface Executeable {

	void execute(EntityManager entityManager);

}

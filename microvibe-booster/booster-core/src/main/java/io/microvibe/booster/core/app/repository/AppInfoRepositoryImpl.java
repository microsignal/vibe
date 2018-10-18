package io.microvibe.booster.core.app.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class AppInfoRepositoryImpl {

	@PersistenceContext
	private EntityManager em;

}

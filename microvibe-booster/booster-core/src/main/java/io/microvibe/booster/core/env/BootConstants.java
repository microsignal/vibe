package io.microvibe.booster.core.env;

import org.springframework.core.Ordered;

public interface BootConstants {

	/**
	 * base-package
	 */
	String BASE_PACKAGE = "com.antengine";

	/**
	 * mybatis-base-package
	 */
	String BASE_PACKAGE_MAPPER = "com.antengine.**.mapper";
	/**
	 * jpa-repository-base-package
	 */
	String BASE_PACKAGE_REPOSITORY = "com.antengine.**.repository";

	// region Entity-Settings
	String ENTITY_PACKAGES_TO_SCAN = "com.antengine.**.entity";
	String ENTITY_ASSIGNABLE_IDENTITY_STRATEGY =
		"io.microvibe.booster.core.base.hibernate.id.AssignableIdentityGenerator";
	String ENTITY_ASSIGNABLE_UUID_STRATEGY =
		"io.microvibe.booster.core.base.hibernate.id.AssignableUUIDGenerator";
	String ENTITY_ASSIGNABLE_UUIDHEX_STRATEGY =
		"io.microvibe.booster.core.base.hibernate.id.AssignableUUIDHexGenerator";
	String ENTITY_ASSIGNABLE_STRATEGY = "org.hibernate.id.Assigned";
	String ENTITY_UUID_STRATEGY = "org.hibernate.id.UUIDGenerator";
	String ENTITY_UUIDHEX_STRATEGY = "org.hibernate.id.UUIDHexGenerator";
	// endregion

	// region aop
	int ASPECT_ORDER_OF_LOG_ASPECT = Ordered.HIGHEST_PRECEDENCE;
	int ASPECT_ORDER_OF_DATASOURCE = -100;
	int ASPECT_ORDER_OF_TRANSACTION = 1;
	int ASPECT_ORDER_OF_DRUID_MONITOR = 100;
	// endregion

}

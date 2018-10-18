package io.microvibe.booster.core.base.resource;

import java.util.Collection;

/**
 * 提供自定义系统资源信息
 *
 * @author Qt
 * @since Jun 06, 2018
 */
public interface ResourceSupplier {

	Collection<ScannedResource> supply();
}

package io.microvibe.booster.pay.mapper;

import io.microvibe.booster.core.base.mybatis.mapping.BaseMapper;
import io.microvibe.booster.pay.entity.BasePayEntity;

public interface BasePayMapper<T extends BasePayEntity> extends BaseMapper<T , Long> {
}

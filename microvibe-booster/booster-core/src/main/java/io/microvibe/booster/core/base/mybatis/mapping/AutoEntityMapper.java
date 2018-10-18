package io.microvibe.booster.core.base.mybatis.mapping;

import io.microvibe.booster.core.base.mybatis.annotation.SuperMapper;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;

@SuperMapper
public interface AutoEntityMapper<T extends Persistable<ID>, ID extends Serializable> extends EntityMapper<T, ID> {

}

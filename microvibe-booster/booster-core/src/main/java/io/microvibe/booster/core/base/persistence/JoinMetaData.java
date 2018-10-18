package io.microvibe.booster.core.base.persistence;

import com.alibaba.fastjson.annotation.JSONField;
import io.microvibe.booster.core.base.mybatis.annotation.JoinOn;
import io.microvibe.booster.core.base.mybatis.lang.LangPatterns;
import io.microvibe.booster.core.base.mybatis.lang.SQLToolkit;
import io.microvibe.booster.core.lang.velocity.VelocityContextLocal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * @author Qt
 * @since Aug 25, 2018
 */
@Getter
@Setter
@Slf4j
public class JoinMetaData {
	private final JoinOn joinOn;
	@JsonIgnore
	@JSONField(serialize = false, deserialize = false)
	private EntityMetaData entity;
	private Field field;
	private boolean oneToMany = false;
	private JoinOn.JoinType joinType;
	private Class<?> table;
	private EntityMetaData tableMetaData;
	private String tableAlias;
	private String property;
	private String on;
	private String columnPrefix;

	public JoinMetaData(EntityMetaData entity, JoinOn joinOn) {
		if (StringUtils.isBlank(joinOn.property())) {
			throw new IllegalArgumentException("表连接配置[property]属性缺失: " + joinOn.toString());
		}
		this.field =  ReflectionUtils.findField(entity.getEntityClass(), joinOn.property());;
		this.entity = entity;
		this.joinOn = joinOn;
		init();
	}

	public JoinMetaData(EntityMetaData entity, Field field, JoinOn joinOn) {
		this.entity = entity;
		this.field = field;
		this.joinOn = joinOn;
		init();
	}

	private void init() {
		Assert.notNull(entity, "实体元数据不能为空");
		Assert.notNull(field, "实体属性不能为空");
		Assert.notNull(joinOn, "表连接配置不能为空");
		// property
		if (StringUtils.isBlank(joinOn.property())) {
			this.property = this.field.getName();
		} else {
			this.property = joinOn.property();
		}

		Class<?> fieldType = field.getType();
		if (Collection.class.isAssignableFrom(fieldType) || fieldType.isArray()) {
			oneToMany = true;
		} else {
			oneToMany = false;
		}

		this.table = joinOn.table();
		if (this.table == this.entity.getEntityClass()) {
			this.tableMetaData = this.entity;
		} else {
			EntityMetaData cacheMetaData = EntityMetaData.entityCache.get().get(this.table);
			if (cacheMetaData != null) {
				this.tableMetaData = cacheMetaData;
			} else {
				this.tableMetaData = PersistentRecognizer.entityMetaData(this.table);
			}
		}

		// joinType
		this.joinType = joinOn.joinType();

		// tableAlias
		if (StringUtils.isBlank(joinOn.tableAlias())) {
			this.tableAlias = this.property;
		} else {
			this.tableAlias = joinOn.tableAlias();
		}
		// columnPrefix
		if (StringUtils.isBlank(joinOn.columnPrefix())) {
			this.columnPrefix = this.property;
		} else {
			this.columnPrefix = joinOn.columnPrefix();
		}

		// on clause
		this.on = joinOn.on();
		try {
			VelocityContextLocal.put("sql", SQLToolkit.instance());
			this.on = LangPatterns.parse(this.on);
		} catch (RuntimeException e) {
			log.error("parse error : {}", this.on);
		} finally {
			VelocityContextLocal.clear();
		}
	}
}

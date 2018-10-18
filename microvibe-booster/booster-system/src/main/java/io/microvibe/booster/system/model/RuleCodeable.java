package io.microvibe.booster.system.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.core.annotation.AnnotationUtils;

import javax.persistence.Table;

/**
 * @author Qt
 * @since Jul 24, 2018
 */
public interface RuleCodeable {

	@JsonIgnore
	@JSONField(deserialize = false, serialize = false)
	void setEntityCode(String entityCode);

	@JsonIgnore
	@JSONField(deserialize = false, serialize = false)
	String getEntityCode();

	/**
	 * 是否私户
	 * @return
	 */
	default Object getPublicPrivate(){
		return null;
	}

	@JsonIgnore
	@JSONField(deserialize = false, serialize = false)
	default String getRuleCode() {
		Table table = AnnotationUtils.findAnnotation(getClass(), Table.class);
		String name = table.name();
		return name;
	}

}

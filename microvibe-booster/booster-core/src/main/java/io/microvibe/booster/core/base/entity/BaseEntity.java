package io.microvibe.booster.core.base.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.microvibe.booster.core.base.persistence.PersistentRecognizer;
import io.microvibe.booster.core.env.BootConstants;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.domain.Persistable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 抽象实体基类, 所有的实体类均继承自本类.<r>
 * 实际使用中有以下辅助实现类可供选择:
 * <ul>
 * <li>{@link BaseAutoIncrementEntity}: 自增主键实体, 适用于 MySQL、SQL Server、DB2 等有自增主键机制的数据库</li>
 * <li>{@link BaseAutoUuidEntity}: UUID主键实体, 长度36位, 形如 <code>0bfb52f7-0b05-47b3-a921-d11aca57d4f9</code> </li>
 * <li>{@link BaseAutoUuidHexEntity}: UUID主键实体, 长度32位, 形如 <code>8a8a81d45f945fda015f945fdae90000</code> </li>
 * <li>{@link BaseSequenceEntity}: sequence主键实体, 适用于PostgreSQL、Oracle 等, 需要实体实现类配置sequence名称</li>
 * <li>{@link BaseAssignedEntity}: 必须手动为主键赋值</li>
 * <li>{@link BaseAssignableAutoIncEntity}: 支持手动赋值的自增主键实体</li>
 * <li>{@link BaseAssignableAutoUuidEntity}: 支持手动赋值的UUID主键实体</li>
 * <li>{@link BaseAssignableAutoUuidHexEntity}: 支持手动赋值的UUID主键实体</li>
 * </ul>
 *
 * @param <ID>
 * @author Qt
 * @since Nov 07, 2017
 */
@Setter
@Getter
public abstract class BaseEntity<ID extends Serializable>
	implements Persistable<ID>, BootConstants, Cloneable, NullUpdateable {

	private static final long serialVersionUID = 1L;

	@Transient
	@JSONField(serialize = false)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Set<String> updatingNullFields = new LinkedHashSet<>();

	static {
		// ASM机制的JSON序列化与反序列化在遇到含有default方法的接口时会报错
		ParserConfig.getGlobalInstance().setAsmEnable(false);
		SerializeConfig.getGlobalInstance().setAsmEnable(false);
	}

	@Override
	public void addUpdatingNullField(String field) {
		if(updatingNullFields == null){
			updatingNullFields = new LinkedHashSet<>();
		}
		updatingNullFields.add(field);
	}

	@Override
	public void removeUpdatingNullField(String field) {
		if(updatingNullFields != null){
			updatingNullFields.remove(field);
		}
	}

	@Override
	public abstract ID getId();

	public abstract void setId(final ID id);

	@Override
	public boolean isNew() {
		ID id = getId();
		return null == id || id instanceof String && StringUtils.isBlank((String) id);
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!getClass().equals(obj.getClass())) {
			return false;
		}
		BaseEntity<?> that = (BaseEntity<?>) obj;

		return null == this.getId() ? false : this.getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		int hashCode = 17;
		hashCode += null == getId() ? 0 : getId().hashCode() * 31;
		return hashCode;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public String toJSONString() {
		return JSONObject.toJSONString(this);
	}

	public String toJSONPrettyString() {
		return JSONObject.toJSONString(this, SerializerFeature.PrettyFormat);
	}

	@Override
	public BaseEntity<ID> clone() {
		return SerializationUtils.clone(this);
	}

}

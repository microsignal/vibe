package io.microvibe.booster.core.base.entity;

import com.alibaba.fastjson.JSONObject;
import io.microvibe.booster.core.env.BootConstants;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseAssignableAutoUuidEntity extends BaseEntity<String> implements UuidSettable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "assignable-uuid-string")
	@GenericGenerator(name = "assignable-uuid-string",
		strategy = BootConstants.ENTITY_ASSIGNABLE_UUID_STRATEGY)
	private String id/* = UUID.randomUUID().toString()*/;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}

}

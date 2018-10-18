package io.microvibe.booster.core.base.entity;

import com.alibaba.fastjson.JSONObject;
import io.microvibe.booster.core.env.BootConstants;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseAssignableAutoIncEntity extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "assignableIdentity")
	@GenericGenerator(name = "assignableIdentity", strategy = BootConstants.ENTITY_ASSIGNABLE_IDENTITY_STRATEGY)
	private Long id;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}

}

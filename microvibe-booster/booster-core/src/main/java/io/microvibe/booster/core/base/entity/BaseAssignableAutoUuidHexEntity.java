package io.microvibe.booster.core.base.entity;

import com.alibaba.fastjson.JSONObject;
import io.microvibe.booster.core.env.BootConstants;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseAssignableAutoUuidHexEntity extends BaseEntity<String> implements UuidHexSettable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "assignable-uuid-hex")
	@GenericGenerator(name = "assignable-uuid-hex",
		strategy = BootConstants.ENTITY_ASSIGNABLE_UUIDHEX_STRATEGY)
	protected String id/* = UUID.randomUUID().toString().replace("-", "")*/;

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

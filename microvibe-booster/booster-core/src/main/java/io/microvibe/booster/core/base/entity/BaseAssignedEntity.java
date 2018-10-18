package io.microvibe.booster.core.base.entity;

import com.alibaba.fastjson.JSONObject;
import io.microvibe.booster.core.env.BootConstants;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * 实体类基类, ID值需要手动指定
 *
 * @param <ID>
 * @author Qt
 * @since Nov 07, 2017
 */
@MappedSuperclass
public abstract class BaseAssignedEntity<ID extends Serializable> extends BaseEntity<ID> {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "assigned")
	@GenericGenerator(name = "assigned", strategy = BootConstants.ENTITY_ASSIGNABLE_STRATEGY)
	private ID id;

	@Override
	public ID getId() {
		return id;
	}

	@Override
	public void setId(ID id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}

}

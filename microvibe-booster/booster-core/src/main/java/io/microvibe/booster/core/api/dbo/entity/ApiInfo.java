package io.microvibe.booster.core.api.dbo.entity;

import io.microvibe.booster.core.base.entity.BaseAutoIncrementEntity;
import io.microvibe.booster.core.base.hibernate.converter.StringArrayConverter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "api_info")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ApiInfo extends BaseAutoIncrementEntity {

	private static final long serialVersionUID = 1L;
	@Column(name = "api_code")
	private String apiCode;
	@Column(name = "api_name")
	private String apiName;
	@Column(name = "api_alias")
	@Convert(converter = StringArrayConverter.class)
	private String[] apiAlias;
	@Column(name = "api_implementor")
	private String apiImplementor;
	@Column(name = "api_remark")
	private String apiRemark;
	@Column(name = "enabled")
	@ColumnDefault("1")
	private Boolean enabled;

	public String getApiCode() {
		return apiCode;
	}

	public void setApiCode(String apiCode) {
		this.apiCode = apiCode;
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public String[] getApiAlias() {
		return apiAlias;
	}

	public void setApiAlias(String[] apiAlias) {
		this.apiAlias = apiAlias;
	}

	public String getApiImplementor() {
		return apiImplementor;
	}

	public void setApiImplementor(String apiImplementor) {
		this.apiImplementor = apiImplementor;
	}

	public String getApiRemark() {
		return apiRemark;
	}

	public void setApiRemark(String apiRemark) {
		this.apiRemark = apiRemark;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

}

package io.microvibe.booster.core.app.entity;

import io.microvibe.booster.core.base.entity.BaseAutoIncrementEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "app_authz")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AppAuthz extends BaseAutoIncrementEntity {
	private static final long serialVersionUID = 1L;
	@Column(name = "app_info_id")
	private Long appInfoId;
	@Column(name = "txn_code")
	private String txnCode;
	@Column(name = "authorized")
	private Boolean authorized;
	@Column(name = "enabled")
	private Boolean enabled;

	@Column(name = "create_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;
	@Column(name = "update_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateTime;

	public Long getAppInfoId() {
		return appInfoId;
	}

	public void setAppInfoId(Long appInfoId) {
		this.appInfoId = appInfoId;
	}

	public String getTxnCode() {
		return txnCode;
	}

	public void setTxnCode(String txnCode) {
		this.txnCode = txnCode;
	}

	public Boolean getAuthorized() {
		return authorized;
	}

	public void setAuthorized(Boolean authorized) {
		this.authorized = authorized;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}

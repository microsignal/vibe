package io.microvibe.booster.core.app.entity;

import io.microvibe.booster.core.app.enums.AppInfoEncType;
import io.microvibe.booster.core.base.entity.BaseAutoIncrementEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "app_info")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AppInfo extends BaseAutoIncrementEntity {
	private static final long serialVersionUID = 1L;
	@Column(name = "app_id")
	private String appId;
	@Column(name = "app_secret")
	private String appSecret;
	@Column(name = "app_name")
	private String appName;
	@Column(name = "enc_type")
	@Enumerated(EnumType.STRING)
	private AppInfoEncType encType = AppInfoEncType.plain;
	@Column(name = "enc_key")
	private String encKey;
	@Column(name = "sign_token")
	private String signToken;
	@Column(name = "sign_token_url")
	private String signTokenUrl;
	@Column(name = "admin_id")
	private Long adminId;
	@Column(name = "create_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;
	@Column(name = "update_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateTime;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public AppInfoEncType getEncType() {
		return encType;
	}

	public void setEncType(AppInfoEncType encType) {
		this.encType = encType;
	}

	public String getEncKey() {
		return encKey;
	}

	public void setEncKey(String encKey) {
		this.encKey = encKey;
	}

	public String getSignToken() {
		return signToken;
	}

	public void setSignToken(String signToken) {
		this.signToken = signToken;
	}

	public String getSignTokenUrl() {
		return signTokenUrl;
	}

	public void setSignTokenUrl(String signTokenUrl) {
		this.signTokenUrl = signTokenUrl;
	}

	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
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

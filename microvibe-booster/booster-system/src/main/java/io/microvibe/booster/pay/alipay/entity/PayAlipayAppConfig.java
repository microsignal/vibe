package io.microvibe.booster.pay.alipay.entity;

import io.microvibe.booster.pay.entity.BasePayEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Qt
 * @since Aug 30, 2018
 */
@Data
@Entity
@Table(name = "pay_alipay_app_config")
public class PayAlipayAppConfig extends BasePayEntity {

	private static final long serialVersionUID = 1L;

	// 关联的 alipay_config 主键
	@Column(name = "alipay_config_id")
	private Long alipayConfigId;

	// 配置标识
	@Column(name = "identity")
	private String identity;

	// 是否有效
	@Column(name = "valid")
	private Boolean valid;

	// app授权码(仅作记录,使用后失效)
	@Column(name = "app_auth_code")
	private String appAuthCode;

	// app授权令牌
	@Column(name = "app_auth_token")
	private String appAuthToken;
	// app刷新令牌
	@Column(name = "app_refresh_token")
	private String appRefreshToken;

	// 授权商户的AppId（如果有服务窗，则为服务窗的AppId）
	@Column(name = "auth_app_id")
	private String authAppId;
	// 授权商户的ID/授权者的PID
	@Column(name = "auth_user_id")
	private String authUserId;


}

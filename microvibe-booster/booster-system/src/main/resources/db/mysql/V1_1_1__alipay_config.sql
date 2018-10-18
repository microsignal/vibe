/********************************************************************
 * 支付宝参数配置表
 ********************************************************************/
DROP TABLE IF EXISTS pay_alipay_config;
CREATE TABLE pay_alipay_config (
	id bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
	identity varchar(63) NOT NULL COMMENT '配置标识符',
	oauth2_url varchar(255) NOT NULL DEFAULT 'https://openauth.alipay.com/oauth2' COMMENT '开放授权地址',
	oauth2_callback_url varchar(255) NOT NULL COMMENT '开放授权回调',
	oauth2_result_url varchar(255) NOT NULL COMMENT '开放授权回调之后的默认跳转页面',
	gateway_url varchar(255) NOT NULL DEFAULT 'https://openapi.alipay.com/gateway.do' COMMENT '支付宝网关',
	app_id varchar(63) NOT NULL COMMENT '应用ID',
	merchant_private_key text NOT NULL COMMENT '商户私钥(PKCS8格式RSA2私钥)',
	partner varchar(63) NOT NULL COMMENT '合作身份者ID',
	seller_id varchar(63) NOT NULL COMMENT '收款支付宝账号',
	alipay_public_key text NOT NULL COMMENT '支付宝公钥',
	notify_url varchar(255) NOT NULL COMMENT '异步通知页面路径',
	return_url varchar(255) NOT NULL COMMENT '同步通知页面路径',
	intro text CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '描述',
	create_time timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	create_user_id bigint unsigned DEFAULT NULL COMMENT '创建人ID',
	update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	update_user_id bigint unsigned DEFAULT NULL COMMENT '更新人ID',
	PRIMARY KEY (id),
	UNIQUE KEY identity (identity),
	KEY app_id (app_id)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='支付宝主参数配置表';

DROP TABLE IF EXISTS pay_alipay_app_config;
CREATE TABLE pay_alipay_app_config (
	id bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
	identity varchar(63) NOT NULL COMMENT '配置标识符',
	alipay_config_id bigint NOT NULL COMMENT '支付宝配置主表ID',
	valid tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效',
	app_auth_code varchar(255) NOT NULL COMMENT 'app授权码(仅作记录,使用后失效)',
	app_auth_token varchar(255) NOT NULL COMMENT 'app授权令牌',
	app_refresh_token varchar(255) NOT NULL COMMENT 'app刷新令牌',
	auth_app_id varchar(255) NOT NULL COMMENT '授权商户的AppId',
	auth_user_id varchar(255) NOT NULL COMMENT '授权商户的ID/授权者的PID',
	create_time timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	create_user_id bigint unsigned DEFAULT NULL COMMENT '创建人ID',
	update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	update_user_id bigint unsigned DEFAULT NULL COMMENT '更新人ID',
	PRIMARY KEY (id),
	UNIQUE KEY identity (identity),
	KEY alipay_config_id (alipay_config_id),
	KEY auth_user_id (auth_user_id),
	KEY auth_app_id (auth_app_id)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='支付宝App授权配置表';


/********************************************************************
 * EOF
 ********************************************************************/

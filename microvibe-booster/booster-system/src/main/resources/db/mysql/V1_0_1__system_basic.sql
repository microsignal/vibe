/********************************************************************
 * 系统日志表
 ********************************************************************/
DROP TABLE IF EXISTS sys_log;
CREATE TABLE sys_log (
	id bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
	user_id bigint unsigned DEFAULT NULL COMMENT '操作用户id',
	user_name varchar(32) DEFAULT NULL COMMENT '操作用户名',
	request_ip varchar(255) DEFAULT NULL COMMENT '请求ip地址',
	request_uri varchar(255) DEFAULT NULL COMMENT '请求uri地址',
	request_method varchar(255) DEFAULT NULL COMMENT '请求方法',
	log_type varchar(255) DEFAULT NULL COMMENT '日志类型',
	log_level varchar(15) DEFAULT 'info' COMMENT '日志级别',
	log_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '日志时间',
	log_module varchar(128) DEFAULT NULL COMMENT '功能模块',
	log_content text CHARACTER SET utf8mb4 COMMENT '日志内容',
	log_stacktrace text CHARACTER SET utf8mb4 COMMENT '日志堆栈',
	class_name varchar(255) DEFAULT NULL COMMENT '执行类名',
	method_name varchar(255) DEFAULT NULL COMMENT '执行方法名',
	method_args text COMMENT '方法入参',
	method_result text COMMENT '方法返回值',
	PRIMARY KEY (id),
	KEY user_id (user_id),
	KEY user_name (user_name),
	KEY log_time (log_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统日志信息';

DROP TABLE IF EXISTS sys_change_log;
CREATE TABLE sys_change_log (
	id bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
	user_id bigint unsigned DEFAULT NULL COMMENT '操作用户id',
	user_name varchar(32) DEFAULT NULL COMMENT '操作用户名',
	request_ip varchar(255) DEFAULT NULL COMMENT '请求ip地址',
	request_uri varchar(255) DEFAULT NULL COMMENT '请求uri地址',
	request_method varchar(255) DEFAULT NULL COMMENT '请求方法',
	change_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '变更时间',
	change_type enum('INSERT','UPDATE','DELETE') DEFAULT 'UPDATE' COMMENT '变更类型',
	change_target varchar(32) DEFAULT NULL COMMENT '变更目标',
	change_intro varchar(255) DEFAULT NULL COMMENT '变更描述',
	old_id varchar(32) DEFAULT NULL COMMENT '原数据ID',
	old_content text CHARACTER SET utf8mb4 COMMENT '原数据内容',
	new_id varchar(32) DEFAULT NULL COMMENT '新数据ID',
	new_content text CHARACTER SET utf8mb4 COMMENT '新数据内容',
	PRIMARY KEY (id),
	KEY user_id (user_id),
	KEY user_name (user_name),
	KEY change_time (change_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统变更日志';

DROP TABLE IF EXISTS sys_err_info;
CREATE TABLE sys_err_info (
	err_id bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '错误号',
	err_user_id bigint unsigned DEFAULT NULL COMMENT '用户ID',
	err_user_account varchar(255) DEFAULT NULL COMMENT '用户账号',
	err_type varchar(255) DEFAULT NULL COMMENT '错误类型',
	err_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '错误时间',
	err_content text CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '错误内容',
	err_stacktrace text CHARACTER SET utf8mb4 COMMENT '错误堆栈',
	PRIMARY KEY (err_id),
	KEY user_id (err_user_id),
	KEY user_name (err_user_account),
	KEY log_time (err_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统异常记录';

DROP TABLE IF EXISTS sys_login_info;
CREATE TABLE sys_login_info (
	login_id bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '登录ID',
	login_user_id bigint unsigned DEFAULT NULL COMMENT '用户ID',
	login_authc_id bigint unsigned DEFAULT NULL COMMENT '用户认证ID',
	login_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '登录时间',
	login_ip varchar(255) DEFAULT NULL COMMENT '登录IP',
	login_country varchar(255) DEFAULT NULL COMMENT '国家',
	login_region varchar(255) DEFAULT NULL COMMENT '地区',
	login_province varchar(255) DEFAULT NULL COMMENT '省',
	login_city varchar(255) DEFAULT NULL COMMENT '市',
	login_network varchar(255) DEFAULT NULL COMMENT '网络类型/运营商',
	PRIMARY KEY (login_id),
	KEY user_id (login_user_id),
	KEY log_time (login_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='登录信息';

/********************************************************************
 * 国际化消息表
 ********************************************************************/
DROP TABLE IF EXISTS sys_msg;
CREATE TABLE sys_msg (
	msg_id bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '消息ID',
	msg_code varchar(128) NOT NULL COMMENT '消息键',
	msg_value varchar(255) NOT NULL COMMENT '消息值',
	msg_deleted tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
	update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	PRIMARY KEY (msg_id),
	UNIQUE KEY i_sys_msg_code (msg_code)
) engine=innodb auto_increment=1000 default charset=utf8mb4 comment='国际化消息';

DROP TABLE IF EXISTS sys_msg_en;
CREATE TABLE sys_msg_en (
	msg_id bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '消息ID',
	msg_code varchar(128) NOT NULL COMMENT '消息键',
	msg_value varchar(255) NOT NULL COMMENT '消息值',
	msg_deleted tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
	update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	PRIMARY KEY (msg_id),
	UNIQUE KEY i_sys_msg_code (msg_code)
) engine=innodb auto_increment=1000 default charset=utf8mb4 comment='国际化消息';

DROP TABLE IF EXISTS sys_msg_zh;
CREATE TABLE sys_msg_zh (
	msg_id bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '消息ID',
	msg_code varchar(128) NOT NULL COMMENT '消息键',
	msg_value varchar(255) NOT NULL COMMENT '消息值',
	msg_deleted tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
	update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	PRIMARY KEY (msg_id),
	UNIQUE KEY i_sys_msg_code (msg_code)
) engine=innodb auto_increment=1000 default charset=utf8mb4 comment='国际化消息';

/********************************************************************
 * 文件表
 ********************************************************************/
DROP TABLE IF EXISTS sys_file;
CREATE TABLE sys_file (
	id varchar(32) NOT NULL COMMENT '文件ID',
	category varchar(32) DEFAULT NULL COMMENT '数据类型',
	file_name varchar(127) DEFAULT NULL COMMENT '文件名称',
	file_type varchar(32) DEFAULT NULL COMMENT '文件类型',
	storage_mode varchar(32) DEFAULT NULL COMMENT '存储方式',
	status varchar(32) DEFAULT NULL COMMENT '文件状态',
	file_path varchar(254) DEFAULT NULL COMMENT '文件存储地址',
	file_content longblob COMMENT '文件存储内容',
	file_hash varchar(64) DEFAULT NULL COMMENT '文件sha1摘要值',
	file_sign text COMMENT '文件签名',
	remark text COMMENT '备注',
	deleted tinyint(1) DEFAULT 0 COMMENT '是否删除',
	create_date timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	update_date timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通用文件表';

/********************************************************************
 * 任务表
 ********************************************************************/
DROP TABLE IF EXISTS sys_job;
CREATE TABLE sys_job (
	id varchar(32) NOT NULL COMMENT 'id',
	name varchar(255) DEFAULT NULL COMMENT '任务',
	cron varchar(255) DEFAULT NULL COMMENT '任务计划',
	status varchar(32) DEFAULT NULL COMMENT '任务状态',
	class_name varchar(255) DEFAULT NULL COMMENT '执行类名',
	method_name varchar(255) DEFAULT NULL COMMENT '执行方法名',
	message text COMMENT '任务状态描述',
	stacktrace text COMMENT '错误堆栈',
	intro text COMMENT '描述介绍',
	enabled tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
	deleted tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
	create_date timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	update_date timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统任务信息';

/********************************************************************
 * 系统会话信息
 ********************************************************************/
DROP TABLE IF EXISTS sys_user_session;
CREATE TABLE sys_user_session (
	id varchar(127) NOT NULL COMMENT '会话ID',
	user_id bigint unsigned DEFAULT NULL COMMENT '用户ID',
	authc_channel varchar(63) DEFAULT NULL COMMENT '认证方式',
	authc_code varchar(127) DEFAULT NULL COMMENT '认证账号',
	host varchar(63) DEFAULT NULL COMMENT '登录IP',
	system_host varchar(63) DEFAULT NULL COMMENT '登录主机',
	user_agent varchar(1024) DEFAULT NULL COMMENT '用户浏览器类型',
	user_agent_type varchar(63) DEFAULT NULL COMMENT '浏览器终端类型',
	status varchar(63) NOT NULL COMMENT '在线状态',
	start_timestamp timestamp NULL DEFAULT NULL COMMENT '会话创建时间',
	last_access_time timestamp NULL DEFAULT NULL COMMENT '会话最后访问时间',
	timeout bigint DEFAULT NULL COMMENT '超时时间',
	session mediumtext COMMENT '备份的当前用户会话',
	session_persist_time bigint DEFAULT 0,
	deleted tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
	create_date timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	update_date timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (id),
	KEY i_authc_id (authc_channel,authc_code),
	KEY u_user_id (user_id,status),
	KEY i_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户在线会话信息表';

DROP TABLE IF EXISTS sys_user_last_session;
CREATE TABLE sys_user_last_session (
	user_id bigint unsigned NOT NULL COMMENT '用户ID',
	authc_channel varchar(63) NOT NULL COMMENT '认证方式',
	authc_code varchar(127) NOT NULL COMMENT '认证账号',
	last_session_id varchar(127) DEFAULT NULL COMMENT '最近在线会话ID',
	host varchar(63) DEFAULT NULL COMMENT '登录IP',
	system_host varchar(63) DEFAULT NULL COMMENT '登录时系统IP',
	user_agent varchar(1024) DEFAULT NULL COMMENT '用户浏览器类型',
	user_agent_type varchar(63) DEFAULT NULL COMMENT '浏览器终端类型',
	status varchar(63) NOT NULL COMMENT '在线状态',
	last_login_timestamp timestamp NULL DEFAULT NULL COMMENT '最后登录时间',
	last_stop_timestamp timestamp NULL DEFAULT NULL COMMENT '最后退出时间',
	login_count bigint DEFAULT 0 COMMENT '登录次数',
	total_online_time bigint DEFAULT 0 COMMENT '总的在线时长(秒)',
	deleted tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
	create_date timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	update_date timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (user_id),
	KEY i_authc_id (authc_channel,authc_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户最近在线信息表';

/********************************************************************
 * EOF
 ********************************************************************/





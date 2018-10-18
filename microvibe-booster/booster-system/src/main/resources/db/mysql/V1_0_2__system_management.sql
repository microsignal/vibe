/********************************************************************
 * 系统用户表(兼容老系统)
 ********************************************************************/
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
	user_id bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID',
	user_type varchar(127) NOT NULL DEFAULT 'manager' COMMENT '用户类型',
	user_account varchar(255) NOT NULL COMMENT '用户本地账号',
	user_nickname varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '用户昵称',
	user_locked tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否锁定',
	user_deleted tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
	user_desc varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '描述',
	create_time timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	create_user_id bigint unsigned DEFAULT NULL COMMENT '创建人ID',
	update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	update_user_id bigint unsigned DEFAULT NULL COMMENT '更新人ID',
	PRIMARY KEY (user_id),
	UNIQUE KEY user_account (user_account),
	KEY create_time (create_time)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

DROP TABLE IF EXISTS sys_user_authc;
CREATE TABLE sys_user_authc (
	authc_id bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '认证ID',
	user_id bigint unsigned NOT NULL COMMENT '用户ID',
	authc_channel varchar(127) NOT NULL DEFAULT 'local' COMMENT '认证渠道',
	account varchar(127) DEFAULT NULL COMMENT '账号',
	password varchar(127) DEFAULT NULL COMMENT '密码',
	credentials_salt varchar(255) DEFAULT NULL COMMENT '加密盐',
	create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	create_user_id bigint unsigned DEFAULT NULL COMMENT '创建人ID',
	update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	update_user_id bigint unsigned DEFAULT NULL COMMENT '更新人ID',
	PRIMARY KEY (authc_id),
	UNIQUE KEY i_sys_user_authc_1 (user_id,authc_channel,account)
) ENGINE=InnoDB AUTO_INCREMENT=255 DEFAULT CHARSET=utf8 COMMENT='用户认证信息';

DROP TABLE IF EXISTS sys_user_info;
CREATE TABLE sys_user_info (
	info_id bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
	user_id bigint unsigned NOT NULL COMMENT '用户ID',
	sex char(1) DEFAULT NULL COMMENT '性别',
	birthday date DEFAULT NULL COMMENT '出生日期',
	telephone varchar(63) DEFAULT NULL COMMENT '电话',
	email varchar(127) DEFAULT NULL COMMENT '邮箱',
	address varchar(127) DEFAULT NULL COMMENT '住址',
	update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	update_user_id bigint unsigned DEFAULT NULL COMMENT '更新人ID',
	PRIMARY KEY (info_id),
	UNIQUE KEY i_sys_user_info_1 (user_id)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='用户扩展信息';

/********************************************************************
 * 系统角色表
 ********************************************************************/
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
	id bigint unsigned NOT NULL COMMENT 'id',
	identity varchar(127) NOT NULL COMMENT '角色标识',
	name varchar(127) NOT NULL COMMENT '角色名称',
	intro varchar(255) DEFAULT NULL COMMENT '描述介绍',
	deleted tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
	create_date timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	update_date timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (id),
	UNIQUE KEY u_identity (identity)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

/********************************************************************
 * 系统资源信息表
 ********************************************************************/
DROP TABLE IF EXISTS sys_resource;
CREATE TABLE sys_resource (
	id bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
	parent_id varchar(32) DEFAULT NULL COMMENT '上级资源',
	parent_path varchar(255) DEFAULT NULL COMMENT '上级资源路径',
	identity varchar(127) DEFAULT NULL COMMENT '资源标识',
	name varchar(127) NOT NULL COMMENT '资源名',
	url varchar(255) DEFAULT NULL COMMENT '资源url路径',
	intro varchar(255) DEFAULT NULL COMMENT '描述介绍',
	deleted tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
	create_user bigint DEFAULT NULL COMMENT '创建人',
	create_date timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	update_user bigint DEFAULT NULL COMMENT '修改人',
	update_date timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (id),
	UNIQUE KEY u_identity (identity),
	KEY parent_id (parent_id),
	KEY parent_path (parent_path(191))
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='系统资源信息表';


/********************************************************************
 * 系统菜单信息表
 ********************************************************************/
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
	id bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
	name varchar(127) NOT NULL COMMENT '菜单名',
	type tinyint(4) NOT NULL DEFAULT 1 COMMENT '0栏目1菜单2按钮',
	url varchar(255) DEFAULT NULL COMMENT '菜单url路径',
	resource_identity varchar(127) DEFAULT NULL COMMENT '资源标识',
	parent_id bigint unsigned DEFAULT NULL COMMENT '上级菜单',
	parent_path varchar(255) DEFAULT NULL COMMENT '上级菜单路径',
	icon varchar(127) DEFAULT NULL COMMENT '资源图标',
	order_no int DEFAULT NULL COMMENT '排序字段',
	intro varchar(255) DEFAULT NULL COMMENT '描述介绍',
	visible tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否可见',
	deleted tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
	create_user bigint unsigned DEFAULT NULL COMMENT '创建人',
	create_date timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	update_user bigint unsigned DEFAULT NULL COMMENT '修改人',
	update_date timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (id),
	KEY parent_id (parent_id),
	KEY parent_path (parent_path(191)),
	KEY resource_identity (resource_identity)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='系统菜单信息表';


/********************************************************************
 * 系统权限信息表
 ********************************************************************/
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
	id bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
	user_id bigint unsigned NOT NULL COMMENT '用户id',
	role_id bigint unsigned NOT NULL COMMENT '角色id',
	deleted tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
	create_date timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	update_date timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='系统用户角色映射表';

DROP TABLE IF EXISTS sys_role_resource;
CREATE TABLE sys_role_resource (
	id bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
	role_id bigint unsigned NOT NULL COMMENT '角色id',
	resource_id varchar(32) NOT NULL COMMENT '资源id',
	deleted tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
	create_date timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	update_date timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (id),
	UNIQUE KEY u_role_resource (role_id,resource_id)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='系统角色资源映射表'

DROP TABLE IF EXISTS sys_user_resource;
CREATE TABLE sys_user_resource (
	id bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
	user_id bigint unsigned NOT NULL COMMENT '用户id',
	resource_id bigint unsigned NOT NULL COMMENT '资源id',
	deleted tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
	create_date timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	update_date timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (id),
	UNIQUE KEY u_user_resource (user_id,resource_id)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='系统用户资源映射表';

DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
	id bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
	role_id bigint unsigned NOT NULL COMMENT '角色id',
	menu_id bigint unsigned NOT NULL COMMENT '菜单id',
	deleted tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
	create_date timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	update_date timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (id),
	UNIQUE KEY u_role_menu (role_id,menu_id)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='系统角色菜单映射表';

DROP TABLE IF EXISTS sys_user_menu;
CREATE TABLE sys_user_menu (
	id bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
	user_id bigint unsigned NOT NULL COMMENT '用户id',
	menu_id bigint unsigned NOT NULL COMMENT '菜单id',
	deleted tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
	create_date timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	update_date timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (id),
	UNIQUE KEY u_role_menu (user_id,menu_id)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='系统用户菜单映射表';


/********************************************************************
 * EOF
 ********************************************************************/

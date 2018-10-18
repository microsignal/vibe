package io.microvibe.booster.system.entity;

import io.microvibe.booster.core.base.entity.*;
import io.microvibe.booster.system.enums.Sex;
import io.microvibe.booster.system.enums.UserStatus;
import io.microvibe.booster.system.enums.UserType;
import io.microvibe.booster.system.model.CurrentUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;


/**
 * 系统用户表
 *
 * @author Q
 * @version 1.0
 * @since Jun 22, 2018
 */
@Entity
@Table(name = "sys_user")
@Getter
@Setter
@EqualsAndHashCode
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "user_id")),
})
public class SysUser extends BaseSysCompatibleEntity
	implements DeletedRecordable, CreateDateRecordable, UpdateDateRecordable,
	CreateUserRecordable<Long>, UpdateUserRecordable<Long>,
	CurrentUser<Long> {

	private static final long serialVersionUID = 1L;

	// region columns

	@Column(name = "user_deleted", nullable = false)
	private Boolean deleted;// 是否删除

	@Column(name = "user_type")
	@Enumerated(EnumType.STRING)
	private UserType userType = UserType.manager;

	@Column(name = "user_account")
	private java.lang.String username;//用户名

	@Column(name = "user_nickname")
	private java.lang.String nickname;//用户昵称

	@Column(name = "user_locked")
	private java.lang.Boolean locked;//是否锁定

	@Column(name = "user_desc")
	private java.lang.String intro;//描述介绍

//	@Column(name = "admin")
//	private java.lang.Boolean admin;//是否管理员
//
//	@Column(name = "real_name")
//	private java.lang.String realName;//真实姓名
//
//	@Column(name = "status")
//	@Enumerated(EnumType.STRING)
//	private UserStatus status;//用户状态
//
//	@Column(name = "sex")
//	@Enumerated(EnumType.STRING)
//	private Sex sex;//性别
//
//	@Temporal(TemporalType.TIMESTAMP)
//	@Column(name = "birthday")
//	private java.util.Date birthday;//出生年月
//
//	@Column(name = "mobile_phone")
//	private java.lang.String mobilePhone;//手机号
//
//	@Column(name = "mobile_verified")
//	private java.lang.Boolean mobileVerified;//手机号是否验证
//
//	@Column(name = "email")
//	private java.lang.String email;//邮箱
//
//	@Column(name = "email_verified")
//	private java.lang.Boolean emailVerified;//邮箱是否验证
//
//	@Column(name = "identity_no")
//	private java.lang.String identityNo;//身份证号
//
//	@Column(name = "identity_verified")
//	private java.lang.Boolean identityVerified;//身份证号是否验证
//
//	@Column(name = "address")
//	private java.lang.String address;//联系地址
//
//	@Column(name = "photo")
//	private java.lang.String photo;//个人头像
//
//	@Column(name = "signature")
//	private java.lang.String signature;//个性签名

	// endregion columns


	@Override
	public boolean isAdministrator() {
		return UserType.admin.equals(userType);
	}

	/**
	 * 获取用户名称（按：真实姓名、昵称、用户名等优先级）
	 *
	 * @return
	 */
	public String getDisplayName() {
		return (String) coalesce(getNickname(), getUsername());
	}

	private Object coalesce(Object... args) {
		for (Object arg : args) {
			if (arg != null && !"".equals(arg)) {
				return arg;
			}
		}
		return null;
	}
}

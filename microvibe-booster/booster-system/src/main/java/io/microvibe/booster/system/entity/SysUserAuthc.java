package io.microvibe.booster.system.entity;

import io.microvibe.booster.core.base.entity.CreateDateRecordable;
import io.microvibe.booster.core.base.entity.DeletedRecordable;
import io.microvibe.booster.core.base.entity.UpdateDateRecordable;
import io.microvibe.booster.core.base.shiro.authc.AuthcChannel;
import io.microvibe.booster.core.base.shiro.authc.AuthcKit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;


/**
 * 系统用户认证信息表
 *
 * @author Q
 * @version 1.0
 * @since Jun 22, 2018
 */
@Entity
@Table(name = "sys_user_authc")
@Getter
@Setter
@EqualsAndHashCode
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "authc_id")),
})
public class SysUserAuthc extends BaseSysCompatibleEntity
	implements CreateDateRecordable, UpdateDateRecordable, AuthcKit.AuthcToken {

	private static final long serialVersionUID = 1L;

	// region columns

	@Column(name = "user_id")
	private Long userId;//用户id

	@Column(name = "authc_channel")
	@Enumerated(EnumType.STRING)
	private AuthcChannel authcChannel = AuthcChannel.DEFAULT;// 认证方式

	@Column(name = "account")
	private String account;//账号

	@Column(name = "password")
	private String password;//密码

	@Column(name = "credentials_salt")
	private String salt;//加密盐

	// endregion columns


	// region transient
	private transient SysUser sysUser;

	// endregion

}

package io.microvibe.booster.system.entity;

import io.microvibe.booster.core.base.entity.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;


/**
 * 系统用户角色映射信息
 * @since Jun 22, 2018
 * @version 1.0
 * @author Q
 */
@Entity
@Table(name = "sys_user_role")
@Getter
@Setter
@EqualsAndHashCode
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AttributeOverrides(
	@AttributeOverride(name = "id", column = @Column(name = "id"))
)
public class SysUserRole extends BaseSysEntity {

	private static final long serialVersionUID = 1L;

	// region columns

	@Column(nullable = false, name = "user_id")
	private Long userId;// 用户ID
	@Column(nullable = false, name = "role_id")
	private Long roleId;// 角色ID

	// endregion columns

	//	@OneToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "user_id", insertable = false, updatable = false)
//	@LazyToOne(LazyToOneOption.PROXY)
	private transient SysUser sysUser;
	//	@OneToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "role_id", insertable = false, updatable = false)
//	@LazyToOne(LazyToOneOption.PROXY)
	private transient SysRole sysRole;


}

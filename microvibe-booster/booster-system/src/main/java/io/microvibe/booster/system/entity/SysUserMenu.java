package io.microvibe.booster.system.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * 系统用户菜单映射信息
 *
 * @author wz
 * @version 1.0
 * @since Jul 28, 2018
 */
@Entity
@Table(name = "sys_user_menu")
@Getter
@Setter
@EqualsAndHashCode
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AttributeOverrides(
	@AttributeOverride(name = "id", column = @Column(name = "id"))
)
public class SysUserMenu extends BaseSysEntity {

	private static final long serialVersionUID = 1L;

	// region columns
	/**
	 * 用户id
	 */
	@Column(name = "user_id")
	private Long userId;
	/**
	 * 菜单id
	 */
	@Column(name = "menu_id")
	private Long menuId;
	// endregion columns

}


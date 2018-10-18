package io.microvibe.booster.system.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;


/**
 * 系统角色菜单映射信息
 *
 * @author Q
 * @version 1.0
 * @since Jun 22, 2018
 */
@Entity
@Table(name = "sys_role_menu")
@Getter
@Setter
@EqualsAndHashCode
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "id"))
})
public class SysRoleMenu extends BaseSysEntity {

	private static final long serialVersionUID = 1L;

	// region columns

	@Column(name = "role_id")
	private Long roleId;//角色id

	@Column(name = "menu_id")
	private Long menuId;//菜单id

	// endregion columns


}


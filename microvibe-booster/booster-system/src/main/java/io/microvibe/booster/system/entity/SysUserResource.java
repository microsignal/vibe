package io.microvibe.booster.system.entity;

import io.microvibe.booster.core.base.entity.*;
import io.microvibe.booster.system.entity.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * 系统用户权限配置表
 * @since Jul 28, 2018
 * @version 1.0
 * @author wz
 */
@Entity
@Table(name = "sys_user_resource")
@Getter
@Setter
@EqualsAndHashCode
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AttributeOverrides(
	@AttributeOverride(name = "id", column = @Column(name = "id"))
)
public class SysUserResource extends BaseSysEntity{

	private static final long serialVersionUID = 1L;

	// region columns
		/**
		*用户id
		*/
	@Column(name = "user_id")
	private Long userId;
		/**
		*资源id
		*/
	@Column(name = "resource_id")
	private Long resourceId;
	// endregion columns


}


package io.microvibe.booster.system.entity;

import io.microvibe.booster.core.base.entity.DeletedRecordable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;


/**
 * 系统角色表
 *
 * @author Q
 * @version 1.0
 * @since Jun 22, 2018
 */
@Entity
@Table(name = "sys_role")
@Getter
@Setter
@EqualsAndHashCode
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "id")),
})
public class SysRole extends BaseSysEntity implements DeletedRecordable {

	private static final long serialVersionUID = 1L;

	// region columns

	@Column(name = "identity")
	private String identity;//角色标识

	@Column(name = "name")
	private String name;//角色名称

	@Column(name = "intro")
	private String intro;//描述介绍

	// endregion columns

}


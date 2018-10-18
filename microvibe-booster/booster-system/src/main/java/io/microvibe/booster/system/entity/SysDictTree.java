package io.microvibe.booster.system.entity;

import io.microvibe.booster.core.base.entity.EnabledRecordable;
import io.microvibe.booster.core.base.entity.Treeable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * 系统树形字典表
 *
 * @author Q
 * @version 1.0
 * @since Jun 19, 2018
 */
@Entity
@Table(name = "sys_dict_tree")
@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AttributeOverrides(
	@AttributeOverride(name = "id", column = @Column(name = "dict_id"))
)
public class SysDictTree extends BaseSysExtEntity implements EnabledRecordable,
	Treeable<SysDictTree, Long> {

	private static final long serialVersionUID = 1L;

	// region columns
	@Column(name = "type_code")
	private String typeCode;//类型编码

	@Column(name = "dict_code")
	private String dictCode;//字典编码

	@Column(name = "dict_value")
	private String dictValue;//字典值

	@Column(name = "parent_id")
	private Long parentId;//上级id

	@Column(name = "parent_path")
	private String parentPath;//上级id路径

	@Column(name = "order_no")
	private Integer orderNo;//排序号

	@Column(name = "enabled")
	private Boolean enabled;//是否启用

	@Column(name = "description")
	private String description;//描述

	@Transient
	@Getter
	private Set<SysDictTree> children = new LinkedHashSet<>();

	// endregion columns

	public SysDictTree() {
	}

}



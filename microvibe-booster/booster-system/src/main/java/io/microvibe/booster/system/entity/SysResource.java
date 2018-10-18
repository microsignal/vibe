package io.microvibe.booster.system.entity;

import io.microvibe.booster.core.base.entity.Treeable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


/**
 * 系统资源信息表
 *
 * @author Q
 * @version 1.0
 * @since Jun 22, 2018
 */
@Entity
@Table(name = "sys_resource")
@Getter
@Setter
@EqualsAndHashCode
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AttributeOverrides(
	@AttributeOverride(name = "id", column = @Column(name = "id"))
)
public class SysResource extends BaseSysExtEntity implements Treeable<SysResource, Long> {

	private static final long serialVersionUID = 1L;

	// region columns

	@Column(name = "parent_id")
	private Long parentId;//上级资源

	@Column(name = "parent_path")
	private String parentPath;//上级资源路径

	@Column(name = "identity")
	private String identity;//资源标识

	@Column(name = "name")
	private String name;//资源名

	@Column(name = "url")
	private String url;//资源url路径

	@Column(name = "intro")
	private String intro;//描述介绍

	// endregion columns

	// region transient
	private transient List<SysResource> children = new ArrayList<>();
	// endregion constructors


	@Override
	public List<SysResource> getChildren() {
		return children;
	}

	@Override
	public void addChild(SysResource childTree) {
		children.add(childTree);
	}


	public String getText() {
		return name;
	}
}

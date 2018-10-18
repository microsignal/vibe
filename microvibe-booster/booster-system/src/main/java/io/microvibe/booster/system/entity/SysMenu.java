package io.microvibe.booster.system.entity;

import io.microvibe.booster.core.base.entity.CreateDateRecordable;
import io.microvibe.booster.core.base.entity.DeletedRecordable;
import io.microvibe.booster.core.base.entity.Treeable;
import io.microvibe.booster.core.base.entity.UpdateDateRecordable;
import io.microvibe.booster.system.enums.MenuType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * 系统菜单信息表
 *
 * @author Q
 * @version 1.0
 * @since Jun 22, 2018
 */
@Entity
@Table(name = "sys_menu")
@Getter
@Setter
@EqualsAndHashCode
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "id")),
})
public class SysMenu extends BaseSysExtEntity
	implements CreateDateRecordable, UpdateDateRecordable, DeletedRecordable,
	Treeable<SysMenu, Long>, Comparable<SysMenu> {

	private static final long serialVersionUID = 1L;

	// region columns

	@Column(name = "parent_id")
	private Long parentId;//上级菜单

	@Column(name = "parent_path")
	private String parentPath;//上级菜单路径

	@Column(name = "resource_identity")
	private String resourceIdentity;//资源标识

	@Column(name = "name")
	private String name;//菜单名

	@Column(name = "type")
	@Enumerated(EnumType.ORDINAL)
	private MenuType type;// 0 栏目 1 菜单 2 按钮

	@Column(name = "url")
	private String url;//菜单url路径

	@Column(name = "icon")
	private String icon;//资源图标

	@Column(name = "order_no")
	private Integer orderNo;//排序字段

	@Column(name = "intro")
	private String intro;//描述介绍

	@Column(name = "visible")
	private Boolean visible;//是否可见

	private transient List<SysRole> roleList;
	private transient SortedSet<SysMenu> children = new TreeSet<>();

	public SysMenu() {
	}

	@Override
	public int compareTo(SysMenu o) {
		SysMenu o1 = this;
		SysMenu o2 = o;

		if (o1.getOrderNo() == null || o2.getOrderNo() == null) {
			return o1.hashCode() < o2.hashCode() ? -1 : 1;
		}
		if (o1.getOrderNo() > o2.getOrderNo()) {
			return 1;
		}
		if (o1.getOrderNo() < o2.getOrderNo()) {
			return -1;
		}
		if (o1.getName() == null || o2.getName() == null) {
			return o1.hashCode() < o2.hashCode() ? -1 : 1;
		} else {
			return o1.getName().compareTo(o2.getName());
		}
	}

	public boolean beVisible() {
		return Boolean.TRUE.equals(visible);
	}

	@Override
	public void addChild(SysMenu sysMenu) {
		children.add(sysMenu);
	}

	/**
	 * jsTree-text
	 *
	 * @return
	 */
	public String getText() {
		return name;
	}

	/**
	 * jsTree-icon
	 *
	 * @return
	 */
	public String getIcon() {
		return icon;
	}
}

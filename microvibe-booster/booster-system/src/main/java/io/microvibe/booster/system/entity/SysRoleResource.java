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
 * 系统角色权限配置表
 * @since Jun 22, 2018
 * @version 1.0
 * @author Q
 */
@Entity
@Table(name = "sys_role_resource")
@Getter
@Setter
@EqualsAndHashCode
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AttributeOverrides(
	@AttributeOverride(name = "id", column = @Column(name = "id"))
)
public class SysRoleResource extends BaseSysEntity {

	private static final long serialVersionUID = 1L;

	// region columns

	@Column(name = "role_id")
	private Long roleId;//角色id

	@Column(name = "resource_id")
	private Long resourceId;//资源id

	// endregion columns


	private transient SysResource sysResource;
	/*
	@Column(name = "operations")
	private String operations;// 操作列表
	private transient Set<ResourceOperation> operationSet;
	private String toString(Set<ResourceOperation> operationSet) {
		StringBuilder sb = new StringBuilder();
		for (ResourceOperation oper : operationSet) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(oper.name());
		}
		return sb.toString();
	}

	public void addOperation(ResourceOperation oper) {
		if (operationSet == null) {
			operationSet = new LinkedHashSet<>();
		}
		operationSet.add(oper);
	}

	public void setOperationSet(Set<ResourceOperation> operationSet) {
		this.operationSet = operationSet;
		this.operations = toString(operationSet);
	}

	public void setOperations(String operations) {
		String[] arr = operations.split(",");
		Set<ResourceOperation> operationSet = new LinkedHashSet<>();
		for (String s : arr) {
			try {
				ResourceOperation oper = ResourceOperation.valueOf(s);
				operationSet.add(oper);
			} catch (IllegalArgumentException e) {
			}
		}
		this.operationSet = operationSet;
		this.operations = toString(operationSet);
	}

	*/

}

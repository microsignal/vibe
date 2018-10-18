package io.microvibe.booster.system.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;


/**
 * 系统字典表
 *
 * @author Q
 * @version 1.0
 * @since Jun 19, 2018
 */
@Entity
@Table(name = "sys_dict")
@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "dict_id")),
	@AttributeOverride(name = "deleted", column = @Column(name = "dict_deleted")),
	@AttributeOverride(name = "updateDate", column = @Column(name = "update_time")),
	@AttributeOverride(name = "createDate", column = @Column(name = "")),
})
public class SysDict
	extends BaseSysEntity
	/*extends BaseAssignableAutoIncEntity
	implements UpdateDateRecordable, DeletedRecordable
	*/ {
	private static final long serialVersionUID = 1L;

	// region columns
	@Column(name = "dict_type")
	private String dictType;//字典类型

	@Column(name = "dict_name")
	private String dictName;//字典中

	@Column(name = "dict_value")
	private String dictValue;//字典值

//	@Column(name = "order_no")
//	private Integer orderNo;//排序号
//
//	@Column(name = "enabled")
//	private Boolean enabled;//是否启用

	@Column(name = "dict_remark")
	private String description;//描述/备注

//	@Column(name = "update_time")
//	@Temporal(TemporalType.TIMESTAMP)
//	private Date updateDate;// 修改时间

//	@Column(name = "dict_deleted", nullable = false)
//	private Boolean deleted;// 是否删除


	// endregion columns

	public SysDict() {
	}

}



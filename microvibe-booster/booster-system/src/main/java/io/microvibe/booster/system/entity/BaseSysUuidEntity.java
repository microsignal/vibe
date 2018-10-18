package io.microvibe.booster.system.entity;

import io.microvibe.booster.core.base.entity.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author Qt
 * @since Oct 26, 2017
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseSysUuidEntity
	extends BaseAssignableAutoUuidHexEntity
	implements CreateDateRecordable, UpdateDateRecordable, DeletedRecordable {
	private static final long serialVersionUID = 1L;

	@Column(name = "create_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;// 创建时间
	@Column(name = "update_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;// 修改时间

	@Column(name = "deleted", nullable = false)
	private Boolean deleted ;// 是否删除


}

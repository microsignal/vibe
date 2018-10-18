package io.microvibe.booster.pay.entity;

import io.microvibe.booster.core.base.entity.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Qt
 * @since Aug 30, 2018
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BasePayEntity
	extends BaseAssignableAutoIncEntity
	implements CreateDateRecordable, UpdateDateRecordable, CreateUserRecordable<Long>, UpdateUserRecordable<Long> {

	private static final long serialVersionUID = 1L;

	@Column(name = "create_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;// 创建时间
	@Column(name = "update_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;// 修改时间

	@Column(name = "create_user_id")
	private Long createUser;//创建人

	@Column(name = "update_user_id")
	private Long updateUser;//修改人

	@Formula("(select coalesce(user_nickname,user_account) from sys_user x where x.user_id = create_user_id)")
	private String createUserName;

	@Formula("(select coalesce(user_nickname,user_account) from sys_user x where x.user_id = update_user_id)")
	private String updateUserName;

	public Timestamp getCreateTime() {
		Date date = getCreateDate();
		if (date == null) {
			return null;
		}
		return date instanceof Timestamp ? (Timestamp) date : new Timestamp(date.getTime());
	}

	public void setCreateTime(Timestamp timestamp) {
		setCreateDate(timestamp);
	}

	public Timestamp getUpdateTime() {
		Date date = getUpdateDate();
		if (date == null) {
			return null;
		}
		return date instanceof Timestamp ? (Timestamp) date : new Timestamp(date.getTime());
	}

	public void setUpdateTime(Timestamp timestamp) {
		setUpdateDate(timestamp);
	}
}

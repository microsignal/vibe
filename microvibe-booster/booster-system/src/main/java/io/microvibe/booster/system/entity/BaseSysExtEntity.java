package io.microvibe.booster.system.entity;

import io.microvibe.booster.core.base.entity.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * @author Qt
 * @since Oct 26, 2017
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseSysExtEntity
	extends BaseSysEntity
	implements CreateDateRecordable, UpdateDateRecordable, DeletedRecordable,
	CreateUserRecordable<Long>, UpdateUserRecordable<Long> {
	private static final long serialVersionUID = 1L;

	@Column(name = "create_user")
	private Long createUser;//创建人

	@Column(name = "update_user")
	private Long updateUser;//修改人

	@Formula("(select coalesce(user_nickname,user_account) from sys_user x where x.user_id = create_user)")
	private String createUserName;

	@Formula("(select coalesce(user_nickname,user_account) from sys_user x where x.user_id = update_user)")
	private String updateUserName;

}

package io.microvibe.booster.system.entity;

import io.microvibe.booster.core.base.mybatis.annotation.JoinOn;
import lombok.Data;
import org.springframework.data.domain.Persistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Qt
 * @since Aug 25, 2018
 */
@Entity
@Table(name = "sys_user")
@Data
public class A implements Persistable<Long> {

	@Id
	@Column(name = "user_id")
	private Long id;

	@Column(name = "user_account")
	private String userAccount;

	@JoinOn(table = B.class, on = "user_id = info.user_id")
	private B info;

	@Override
	public boolean isNew() {
		return id == null;
	}
}

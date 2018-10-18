package io.microvibe.booster.system.entity;

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
@Table(name = "sys_user_authc")
@Data
public class C implements Persistable<Long> {
	@Id
	@Column(name = "authc_id")
	private Long id;
	private String userId;
	private String password;


	@Override
	public boolean isNew() {
		return id == null;
	}
}

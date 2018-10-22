package io.microvibe.booster.system.entity;

import io.microvibe.booster.core.env.BootConstants;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

/**
 * @author Qt
 * @since Aug 25, 2018
 */
@Entity
@Table(name = "sys_user_info")
@Data
public class B implements Persistable<Long> {
	@Id
	@Column(name = "info_id")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "assignableIdentity")
	@GenericGenerator(name = "assignableIdentity", strategy = BootConstants.ENTITY_ASSIGNABLE_IDENTITY_STRATEGY)
	private Long id;
	private Long userId;
	private String sex;


	@Override
	public boolean isNew() {
		return id == null;
	}
}

package io.microvibe.booster.core.base.entity;

import javax.persistence.*;

/**
 * <p> 使用 sequence 主键生成机制时的抽象实体基类
 * <p> 子类只需要在类头上加 @{@linkplain SequenceGenerator}(name="seq", sequenceName="sequence名称")
 * <p>
 *
 * @author Qt
 * @since Nov 07, 2017
 */
@MappedSuperclass
public abstract class BaseSequenceEntity extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
//    @SequenceGenerator(name = "seq", sequenceName = "seq_general")
	private Long id;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
}

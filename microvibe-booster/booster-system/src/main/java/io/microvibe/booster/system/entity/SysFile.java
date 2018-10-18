package io.microvibe.booster.system.entity;

import io.microvibe.booster.core.base.entity.CreateDateRecordable;
import io.microvibe.booster.core.base.entity.DeletedRecordable;
import io.microvibe.booster.core.base.entity.UpdateDateRecordable;
import io.microvibe.booster.system.enums.FileStatus;
import io.microvibe.booster.system.storage.StorageMode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * 通用文件表
 *
 * @author Q
 * @version 1.0
 * @since Jul 14, 2018
 */
@Entity
@Table(name = "sys_file")
@Getter
@Setter
@EqualsAndHashCode
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AttributeOverrides(
	@AttributeOverride(name = "id", column = @Column(name = "id"))
)
public class SysFile extends BaseSysUuidEntity
	implements DeletedRecordable, CreateDateRecordable, UpdateDateRecordable {

	private static final long serialVersionUID = 1L;

	// region columns
	@Column(name = "category")
	private String category;//数据类型

	@Column(name = "storage_mode")
	@Enumerated(EnumType.STRING)
	private StorageMode storageMode = StorageMode.local;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private FileStatus status = FileStatus.temporary;

	@Column(name = "file_name")
	private String fileName;//文件名称

	@Column(name = "file_type")
	private String fileType;//文件类型

	@Column(name = "file_path")
	private String filePath;//文件存储地址

	@Column(name = "file_content")
	private byte[] fileContent;//文件存储内容

	@Column(name = "file_hash")
	private String fileHash;//文件sha1摘要值

	@Column(name = "file_sign")
	private String fileSign;//文件签名

	@Column(name = "remark")
	private String remark;//备注

	// endregion columns

	// region constructors

	public SysFile() {
	}

	public SysFile(String id) {
		this.id = id;
	}

	// endregion constructors


}


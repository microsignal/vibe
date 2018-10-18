package io.microvibe.booster.system.service;

import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.system.entity.SysFile;
import io.microvibe.booster.system.enums.FileStatus;
import io.microvibe.booster.system.mapper.SysFileMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 通用文件表
 *
 * @author Q
 * @version 1.0
 * @since Jul 14, 2018
 */
@Service
@Slf4j
public class SysFileService extends SysBaseService<SysFile, String> {

	@Autowired
	@BaseComponent
	private SysFileMapper sysFileMapper;

	/**
	 * 修改文件状态为正常状态
	 *
	 * @param fileId
	 */
	public void updateToNormalStatus(String fileId) {
		SysFile sysFile = new SysFile();
		sysFile.setId(fileId);
		sysFile.setStatus(FileStatus.normal);
		updateSelective(sysFile);
	}

	/**
	 * 恢复
	 *
	 * @param sysFile
	 */
	public void recycle(SysFile sysFile) {
		sysFile.setDeleted(false);
		this.update(sysFile);
	}

	/**
	 * 批量恢复
	 *
	 * @param ids
	 */
	public void recycleInBatch(String[] ids) {
		for (String id : ids) {
			SysFile sysFile = this.getById(id);
			if (sysFile != null) {
				this.recycle(sysFile);
			}
		}
	}
}

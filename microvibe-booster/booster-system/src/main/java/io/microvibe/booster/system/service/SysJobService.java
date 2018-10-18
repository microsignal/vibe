package io.microvibe.booster.system.service;

import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.core.base.mybatis.annotation.AfterMybatisScanner;
import io.microvibe.booster.core.schedule.TaskDefinitionDao;
import io.microvibe.booster.core.schedule.TaskRunningState;
import io.microvibe.booster.system.entity.SysJob;
import io.microvibe.booster.system.mapper.SysJobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;

@Service
@AfterMybatisScanner
public class SysJobService extends SysBaseService<SysJob, String> implements TaskDefinitionDao<SysJob> {

	@BaseComponent
	@Autowired
	SysJobMapper sysJobMapper;

	@Override
	public void updateRunningState(String taskId, TaskRunningState state, String message) {
		SysJob job = new SysJob();
		job.setId(taskId);
		job.setStatus(state);
		job.setMessage(message);
		this.updateSelective(job);
	}

	@Override
	public void updateRunningState(String taskId, TaskRunningState state, String message, Throwable e) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		SysJob job = new SysJob();
		job.setId(taskId);
		job.setStatus(state);
		job.setMessage(message);
		job.setStacktrace(stringWriter.toString().substring(0, Short.MAX_VALUE));
		this.updateSelective(job);
	}

}

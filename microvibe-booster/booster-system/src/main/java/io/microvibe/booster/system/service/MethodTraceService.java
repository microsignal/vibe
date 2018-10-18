package io.microvibe.booster.system.service;

import com.alibaba.fastjson.JSON;
import io.microvibe.booster.core.log.IMethodTraceService;
import io.microvibe.booster.core.log.Log;
import io.microvibe.booster.core.log.LogInfo;
import io.microvibe.booster.system.entity.SysLog;
import io.microvibe.booster.system.enums.LogType;
import io.microvibe.booster.system.toolkit.Users;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

//@Component
public class MethodTraceService implements IMethodTraceService {

	@Autowired
	SysLogService sysLogService;

	@Override
	public boolean logForThrowing(LogInfo info) {
		if (info.hasPointcut(Log.Pointcut.Before)) {
			SysLog log = new SysLog();
			//log.setId(info.getUuid());
			log.setId((Long) info.getAttachment());
			log.setLogStacktrace(info.getThrowableTraceString());
			log.setLogLevel(Log.Level.ERROR);
			sysLogService.updateSelective(log);
		} else {
			SysLog log = toSysLog(info);
			log.setLogLevel(Log.Level.ERROR);
			sysLogService.insertSelective(log);
		}
		return false;
	}

	@Override
	public boolean logForAfter(LogInfo info) {
		if (info.hasPointcut(Log.Pointcut.Before)) {
			SysLog log = new SysLog();
			//log.setId(info.getUuid());
			log.setId((Long) info.getAttachment());
			log.setMethodResult(JSON.toJSONString(info.getMethodReturnValue()));
			sysLogService.updateSelective(log);
		} else {
			sysLogService.insertSelective(toSysLog(info));
		}
		return false;
	}

	@Override
	public boolean logForBefore(LogInfo info) {
		SysLog log = toSysLog(info);
		sysLogService.insertSelective(log);
		Long id = log.getId();
		if (id != null) {
			info.setAttachment(id);
		}
		return false;
	}

	private SysLog toSysLog(LogInfo info) {
		SysLog log = new SysLog();
		//log.setId(info.getUuid());
		log.setLogType(LogType.AUDIT);
		log.setLogLevel(info.getLevel());
		log.setLogTime(new Date());
		log.setLogModule(info.getModule());
		log.setLogContent(info.getContent());
		log.setClassName(info.getSignature().getDeclaringTypeName());
		log.setMethodName(info.getMethodName());
		log.setRequestIp(info.getRequestIp());
		log.setRequestUri(info.getRequestUri());
		log.setRequestMethod(info.getRequestMethod());
		log.setMethodArgs(info.getMethodArgsString());
		if (Users.isOnline()) {
			log.setUserId(Users.getCurrentUserId());
			log.setUserName(Users.getCurrentUser().getUsername());
		}
		log.setMethodResult(info.getMethodReturnValueString());
		log.setLogStacktrace(info.getThrowableTraceString());
		return log;
	}

}

package io.microvibe.booster.system.task;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.core.base.mybatis.annotation.AfterMybatisScanner;
import io.microvibe.booster.core.base.service.PlainSqlService;
import io.microvibe.booster.system.service.SysUserSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Qt
 * @since Aug 07, 2018
 */
@Component
@Slf4j
@AfterMybatisScanner
public class AdvanceSystemTaskAction {

	@Autowired
	private SysUserSessionService sysUserSessionService;

	@Scheduled(cron = "0 0 * * * ?")
	public void cleanDeletedSession() {
		log.info("准备清理1周之前的待删除会话...");
		Date date = new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000);

		PlainSqlService sqlService = ApplicationContextHolder.getBean(PlainSqlService.class);
		Map<String, Object> param = new HashMap<>();
		param.put("currentDate", date);
		param.put("sql", "delete from sys_user_session " +
			"where deleted = 1 and user_id is null and  last_access_time <= #{currentDate}");
		int rowCount = sqlService.update(param);
		log.info("已清理会话数: {}", rowCount);

		log.info("清理1周之前的待删除会话完毕");
	}

	@Scheduled(cron = "0 0/10 * * * ?")
	public void cleanTmpSession() {
		log.info("扫描系统临时会话, 准备清理10分钟前的数据...");
		Date date = new Date(System.currentTimeMillis() - 10 * 60 * 1000);

		PlainSqlService sqlService = ApplicationContextHolder.getBean(PlainSqlService.class);
		Map<String, Object> param = new HashMap<>();
		param.put("currentDate", date);
		param.put("sql", "update sys_user_session set deleted = 1 " +
			"where deleted = 0 and user_id is null and  last_access_time <= #{currentDate}");
		int rowCount = sqlService.update(param);
		log.info("已清理会话数: {}", rowCount);

		log.info("系统临时会话清理完毕");
	}

}

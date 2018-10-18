package io.microvibe.booster.core.base.datasource;

import io.microvibe.booster.core.env.BootConstants;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(BootConstants.ASPECT_ORDER_OF_DATASOURCE)// 保证该AOP在@Transactional之前执行
@Component
@Slf4j //https://blog.csdn.net/catoop/article/details/50575038
public class DynamicDataSourceAspect {

	@Before("@within(ds) || @annotation(ds)")
	public void changeDataSource(JoinPoint point, TargetDataSource ds) throws Throwable {
		if (ds != null) {
			String dsId = ds.value();
			if (!DynamicDataSourceContextHolder.containsDataSource(dsId)) {
				log.error("数据源[{}]不存在，使用默认数据源 >>> {}", dsId, point.getSignature());
			} else {
				log.debug("Use DataSource : {} > {}", dsId, point.getSignature());
				DynamicDataSourceContextHolder.setDataSourceType(dsId);
			}
		}
	}

	@After("@within(ds) || @annotation(ds)")
	public void restoreDataSource(JoinPoint point, TargetDataSource ds) {
		if (ds != null) {
			String dsId = ds.value();
			log.debug("Revert DataSource : {} >>> {}", dsId, point.getSignature());
			DynamicDataSourceContextHolder.clearDataSourceType();
		}
	}

	@Before("@within(ds) || @annotation(ds)")
	public void changeDataSource(JoinPoint point, MemoryDataSource ds) throws Throwable {
		if (ds != null) {
			String dsId = MemoryDataSource.ID;
			if (!DynamicDataSourceContextHolder.containsDataSource(dsId)) {
				log.error("数据源[{}]不存在，使用默认数据源 >>> {}", dsId, point.getSignature());
			} else {
				log.debug("Use DataSource : {} > {}", dsId, point.getSignature());
				DynamicDataSourceContextHolder.setDataSourceType(dsId);
			}
		}
	}

	@After("@within(ds) || @annotation(ds)")
	public void restoreDataSource(JoinPoint point, MemoryDataSource ds) {
		if (ds != null) {
			String dsId = MemoryDataSource.ID;
			log.debug("Revert DataSource : {} >>> {}", dsId, point.getSignature());
			DynamicDataSourceContextHolder.clearDataSourceType();
		}
	}
}

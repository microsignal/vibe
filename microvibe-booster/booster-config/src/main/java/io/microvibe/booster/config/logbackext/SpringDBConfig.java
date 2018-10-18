package io.microvibe.booster.config.logbackext;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.db.DBAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.db.DataSourceConnectionSource;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Iterator;
import java.util.List;

@Configuration
public class SpringDBConfig {

//	@Bean
//	public DBAppender dbAppender(DataSource dataSource) {
//		DBAppender dbAppender = new DBAppender();
//		DataSourceConnectionSource connectionSource = new DataSourceConnectionSource();
//		connectionSource.setDataSource(dataSource);
//		connectionSource.start();
//		dbAppender.setConnectionSource(connectionSource);
//		dbAppender.start();
//
//		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
//
//		List<Logger> loggerList = loggerContext.getLoggerList();
//		for (Logger log : loggerList) {
//			Iterator<Appender<ILoggingEvent>> iter = log.iteratorForAppenders();
//			while (iter.hasNext()) {
//				Appender<ILoggingEvent> appender = iter.next();
//				if (appender instanceof DBAppender) {
//					log.addAppender(dbAppender);
//					log.detachAppender(appender);
//				}
//			}
//		}
//		return dbAppender;
//	}

	@Bean
	@ConditionalOnBean(DataSource.class)
	public DataSourceConnectionSource connectionSource(DataSource dataSource) {
		DataSourceConnectionSource connectionSource = new DataSourceConnectionSource();
		connectionSource.setDataSource(dataSource);
		connectionSource.start();

		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

		List<Logger> loggerList = loggerContext.getLoggerList();
		for (Logger log : loggerList) {
			Iterator<Appender<ILoggingEvent>> iter = log.iteratorForAppenders();
			while (iter.hasNext()) {
				Appender<ILoggingEvent> appender = iter.next();
				if (appender instanceof DBAppender) {
					((DBAppender) appender).setConnectionSource(connectionSource);
					appender.start();
				}
			}
		}
		return connectionSource;
	}
}

package io.microvibe.booster.core.log.logback;

import ch.qos.logback.classic.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Logger.class)
public class LogbackAutoConfigure {

	@Bean
	@ConditionalOnMissingBean(LogbackOperator.class)
	public LogbackOperator logbackOperator() {
		return new LogbackOperator();
	}
}

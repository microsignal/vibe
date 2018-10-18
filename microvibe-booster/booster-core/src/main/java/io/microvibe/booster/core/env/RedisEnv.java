package io.microvibe.booster.core.env;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
//@ConfigurationProperties(prefix = "redis")
public class RedisEnv {
	@Value("${redis.enabled:false}")
	boolean enabled;
	@Value("${redis.host:127.0.0.1}")
	String hostName;
	@Value("${redis.port:6379}")
	int port;
	@Value("${redis.dbindex:0}")
	int database;
	@Value("${redis.password:}")
	String password;
	@Value("${redis.publicKey:}")
	String publicKey;

	@Value("${redis.maxIdle:50}")
	int maxIdle;
	@Value("${redis.maxWait:3000}")
	long maxWaitMillis;
	@Value("${redis.testOnBorrow:true}")
	boolean testOnBorrow;


}

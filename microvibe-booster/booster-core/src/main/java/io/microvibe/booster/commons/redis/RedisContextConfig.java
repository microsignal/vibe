package io.microvibe.booster.commons.redis;

import redis.clients.jedis.Protocol;

import java.util.Set;

public class RedisContextConfig {
	private String host = Protocol.DEFAULT_HOST;
	private int port = Protocol.DEFAULT_PORT;
	private String password;
	private String publicKey;
	private int database = Protocol.DEFAULT_DATABASE;
	private int timeout = Protocol.DEFAULT_TIMEOUT;
	private Integer maxTotal;
	private Long maxWaitMillis;
	private Integer maxIdle;
	private Integer minIdle;
	private Integer timeBetweenEvictionRunsMillis;
	private boolean sentinel = false;
	private Set<String> sentinels;
	private String masterName;
	private boolean testOnBorrow;

	public RedisContextConfig() {
		this.host = System.getProperty("redis.host", Protocol.DEFAULT_HOST);
		Integer port = getInteger("redis.port");
		if (port != null) {
			this.port = port.intValue();
		}
		Integer database = getInteger("redis.dbindex");
		if (database != null) {
			this.database = database.intValue();
		}
		String password = System.getProperty("redis.password");
		if (password != null) {
			this.password = password;
		}
		String publicKey = System.getProperty("redis.publicKey");
		if (publicKey != null) {
			this.publicKey = publicKey;
		}
		Integer maxIdle = getInteger("redis.maxIdle");
		if (maxIdle != null) {
			this.maxIdle = maxIdle;
		}
		Long maxWait = getLong("redis.maxWait");
		if (maxWait != null) {
			this.maxWaitMillis = maxWait;
		}
		Boolean testOnBorrow = getBoolean("redis.testOnBorrow");
		if (testOnBorrow != null) {
			this.testOnBorrow = testOnBorrow;
		}
	}

	private Long getLong(String key) {
		String val = System.getProperty(key);
		if (val == null) {
			return null;
		}
		try {
			return Long.valueOf(val);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private Integer getInteger(String key) {
		String val = System.getProperty(key);
		if (val == null) {
			return null;
		}
		try {
			return Integer.valueOf(val);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private Boolean getBoolean(String key) {
		String val = System.getProperty(key);
		if (val == null) {
			return null;
		}
		return Boolean.valueOf(val);
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public int getDatabase() {
		return database;
	}

	public String getHost() {
		return host;
	}

	public String getMasterName() {
		return masterName;
	}

	public Integer getMaxIdle() {
		return maxIdle;
	}

	public Integer getMaxTotal() {
		return maxTotal;
	}

	public Long getMaxWaitMillis() {
		return maxWaitMillis;
	}

	public Integer getMinIdle() {
		return minIdle;
	}

	public String getPassword() {
		return password;
	}

	public int getPort() {
		return port;
	}

	public Set<String> getSentinels() {
		return sentinels;
	}

	public Integer getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	public int getTimeout() {
		return timeout;
	}

	public boolean isSentinel() {
		return sentinel;
	}

	public void setDatabase(int database) {
		this.database = database;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}

	public void setMaxIdle(Integer maxIdle) {
		this.maxIdle = maxIdle;
	}

	public void setMaxTotal(Integer maxTotal) {
		this.maxTotal = maxTotal;
	}

	public void setMaxWaitMillis(Long maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}

	public void setMinIdle(Integer minIdle) {
		this.minIdle = minIdle;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setSentinel(boolean sentinel) {
		this.sentinel = sentinel;
	}

	public void setSentinels(Set<String> sentinels) {
		this.sentinels = sentinels;
	}

	public void setTimeBetweenEvictionRunsMillis(Integer timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

}

package io.microvibe.booster.system.toolkit;

import io.microvibe.booster.commons.redis.RedisContexts;
import io.microvibe.booster.system.enums.SerialDateType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Qt
 * @since Jul 18, 2018
 */
public class Serials {
	private static final int KEEP_YEAR_SECONDS = 3600 * 24 * 365 * 2;
	private static final int KEEP_MONTH_SECONDS = 3600 * 24 * 31 * 2;
	private static final int KEEP_DAY_SECONDS = 3600 * 24 * 3;

	private static final Serial DEFAULT = Serial.get();

	public static Serial getSerial() {
		return DEFAULT;
	}

	public static Serial getSerial(String namespace) {
		return Serial.get(namespace);
	}

	public static SerialNo next(String prefix, SerialDateType dateType) throws JedisException {
		return DEFAULT.next(prefix, dateType);
	}

	/**
	 * prefix + date + rand + seq
	 */
	public static String sequence(String prefix, SerialNo serialNo, int randomLength, int seqLength) {
		return DEFAULT.sequence(prefix, serialNo, randomLength, seqLength);
	}

	/**
	 * prefix + date + rand + seq
	 */
	public static String sequence(String prefix, SerialDateType dateType, int randomLength, String ref, int seqLength) {
		return DEFAULT.sequence(prefix, dateType, randomLength, seqLength);
	}

	public static String sequence(String prefix, SerialDateType dateType, int randomLength, int seqLength) {
		return DEFAULT.sequence(prefix, dateType, randomLength, seqLength);
	}

	public static String sequence(String prefix, SerialDateType dateType, int seqLength) {
		return DEFAULT.sequence(prefix, dateType, seqLength);
	}

	public static String sequence(String prefix, int seqLength) {
		return DEFAULT.sequence(prefix, seqLength);
	}

	public static String sequence(String prefix) {
		return DEFAULT.sequence(prefix);
	}

	/**
	 * 获取用户默认用户名(9位)
	 */
	public static String getUserCode() {
		return sequence("U", SerialDateType.NONE, 1, null, 7);
	}

	/**
	 * 获取序列号(18位)
	 */
	public static String getSN() {
		return sequence("SN", SerialDateType.YYYYMMDD, 1, null, 7);
	}

	/**
	 * 获取申请编号(18位)
	 */
	public static String getAN() {
		return sequence("AN", SerialDateType.YYYYMMDD, 1, null, 7);
	}

	/**
	 * 获取协议编号(18位)
	 */
	public static String getPN() {
		return sequence("PN", SerialDateType.YYYYMMDD, 1, null, 7);
	}

	@Setter
	@Getter
	public static class Serial {
		public static final String DEFAULT_NAMESPACE = "SerialNo";
		private static final Map<String, Serial> pool = Collections.synchronizedMap(new WeakHashMap<>());
		private String namespace;

		private Serial(String namespace) {
			this.namespace = namespace;
		}

		public static Serial get() {
			return get(DEFAULT_NAMESPACE);
		}

		public static Serial get(String namespace) {
			Serial serial = pool.get(namespace);
			if (serial == null) {
				serial = pool.get(namespace);
				if (serial == null) {
					synchronized (Serial.class) {
						pool.put(namespace, serial = new Serial(namespace));
					}
				}
			}
			return serial;
		}

		public void reset(String prefix, SerialDateType dateType, Long seqNo) throws JedisException {
			String date = dateType.now();
			final int expire = getExpire(dateType);
			RedisContexts.run(jedis -> {
				String key = namespace + ":" + (StringUtils.isBlank(prefix) ? "" : prefix + ":") + date;
				if (expire > 0) {
					jedis.setex(key, expire, seqNo.toString());
				} else {
					jedis.set(key, seqNo.toString());
				}
			});
		}

		public SerialNo next(String prefix, SerialDateType dateType) throws JedisException {
			String date = dateType.now();
			final int expire = getExpire(dateType);
			Long seqNo = RedisContexts.call(jedis -> {
				String key = namespace + ":" + (StringUtils.isBlank(prefix) ? "" : prefix + ":") + date;
				Long incr = jedis.incr(key);
				if (expire > 0 && jedis.ttl(key) < 0) {
					jedis.expire(key, expire);
				}
				return incr;
			});
			SerialNo serialNo = new SerialNo();
			serialNo.setPrefix(prefix);
			serialNo.setDate(date);
			serialNo.setSerialNo(seqNo);
			return serialNo;
		}

		private int getExpire(SerialDateType dateType) {
			final int expire;
			if (dateType.hasDay()) {
				expire = KEEP_DAY_SECONDS;
			} else if (dateType.hasMonth()) {
				expire = KEEP_MONTH_SECONDS;
			} else if (dateType.hasYear()) {
				expire = KEEP_YEAR_SECONDS;
			} else {
				expire = -1;
			}
			return expire;
		}

		/**
		 * prefix + date + rand + seq
		 */
		public String sequence(String prefix, SerialNo serialNo, int randomLength, int seqLength) {
			String date = serialNo.getDate();
			String seq = Long.toString(serialNo.getSerialNo());
			StringBuilder sb = new StringBuilder();
			if (prefix != null && prefix.length() > 0) {
				sb.append(prefix);
			}
			sb.append(date);
			if (randomLength > 0) {
				sb.append(RandomStringUtils.randomAlphabetic(randomLength).toUpperCase());
			} else if (randomLength < 0) {
				sb.append(RandomStringUtils.randomNumeric(-randomLength).toUpperCase());
			}
			if (seqLength <= 0) {
				seqLength = 6;
			}
			if (seq.length() > seqLength) {
				sb.append(seq.substring(seq.length() - seqLength));
			} else {
				for (int i = 0; i < seqLength - seq.length(); i++) {
					sb.append("0");
				}
				sb.append(seq);
			}
			return sb.toString();
		}

		/**
		 * prefix + date + rand + seq
		 */
		public String sequence(String prefix, SerialDateType dateType, int randomLength, String ref, int seqLength) {
			if (ref == null) {
				SerialNo serialNo = next(prefix, dateType);
				return sequence(prefix, serialNo, randomLength, seqLength);
			} else {
				String date = dateType.now();
				String seq = ref.toString();

				StringBuilder sb = new StringBuilder();
				if (prefix != null && prefix.length() > 0) {
					sb.append(prefix);
				}
				sb.append(date);
				if (randomLength > 0) {
					sb.append(RandomStringUtils.randomAlphanumeric(randomLength));
				}
				if (seqLength <= 0) {
					seqLength = 6;
				}
				if (seq.length() > seqLength) {
					sb.append(seq);
				} else {
					for (int i = 0; i < seqLength - seq.length(); i++) {
						sb.append("0");
					}
					sb.append(seq);
				}
				return sb.toString();
			}
		}

		public String sequence(String prefix, SerialDateType dateType, int randomLength, int seqLength) {
			SerialNo serialNo = next(prefix, dateType);
			return sequence(prefix, serialNo, randomLength, seqLength);
		}

		public String sequence(String prefix, SerialDateType dateType, int seqLength) {
			SerialNo serialNo = next(prefix, dateType);
			return sequence(prefix, serialNo, 0, seqLength);
		}

		public String sequence(String prefix, int seqLength) {
			SerialNo serialNo = next(prefix, SerialDateType.YYMMDD);
			return sequence(prefix, serialNo, 0, seqLength);
		}

		public String sequence(String prefix) {
			return sequence(prefix, 6);
		}
	}

	@Getter
	@Setter
	public static class SerialNo {
		private String prefix;
		private String date;
		private Long serialNo;
	}

}

package io.microvibe.booster.core.base.shiro.authc;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;

public class AuthcKit {

	public static final String USERNAME_PATTERN = "^[\\u4E00-\\u9FA5\\uf900-\\ufa2d_a-zA-Z][\\u4E00-\\u9FA5\\uf900-\\ufa2d\\w]{1,19}$";
	public static final String EMAIL_PATTERN = "^((([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+(\\.([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+)*)|((\\x22)((((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(([\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x7f]|\\x21|[\\x23-\\x5b]|[\\x5d-\\x7e]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(\\\\([\\x01-\\x09\\x0b\\x0c\\x0d-\\x7f]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF]))))*(((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(\\x22)))@((([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.)+(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.?";
	public static final String MOBILE_PHONE_NUMBER_PATTERN = "^1[3-9][0-9]{9}$";
	public static final int USERNAME_MIN_LENGTH = 1;
	public static final int USERNAME_MAX_LENGTH = 30;
	private static AuthcType defaultAuthcType = AuthcType.bcrypt;
	private static final ThreadLocal<AuthcType> LOCAL = ThreadLocal.withInitial(() -> getDefaultAuthcType());

	public static AuthcType getDefaultAuthcType() {
		return defaultAuthcType;
	}

	public static void setDefaultAuthcType(AuthcType defaultAuthcType) {
		AuthcKit.defaultAuthcType = defaultAuthcType;
	}

	public static void useAuthcType(AuthcType authcType) {
		LOCAL.set(authcType);
	}

	public static void clearAuthcType() {
		LOCAL.remove();
	}

	/**
	 * 使用指定盐值对密码加密
	 *
	 * @param password 明文
	 * @param salt     加密盐
	 * @return 加密后的密文
	 */
	public static String encrypt(String password, String salt) {
		return LOCAL.get().getEncryptor().encrypt(password, salt);
	}

	/**
	 * 在对象{@linkplain AuthcToken}上生成新的盐值与密码密文
	 *
	 * @param authc    用户认证对象
	 * @param password 密码明文
	 * @return
	 */
	public static <T extends AuthcToken> T encrypt(T authc, String password) {
		return LOCAL.get().getEncryptor().encrypt(authc, password);
	}

	/**
	 * 校验参数提供的密码的正确性
	 *
	 * @param tryPassword 待校验密码值
	 * @param salt        密码盐值
	 * @param password    密码密文
	 * @return
	 */
	public static boolean matches(String tryPassword, String salt, String password) {
		return LOCAL.get().getEncryptor().matches(tryPassword, salt, password);
	}

	/**
	 * 校验密码
	 *
	 * @param authc       用户认证对象, 内含密码密文及其盐值
	 * @param tryPassword 待校验密码值
	 * @return
	 */
	public static boolean matches(AuthcToken authc, String tryPassword) {
		return LOCAL.get().getEncryptor().matches(authc, tryPassword);
	}

	/**
	 * 校验是否匹配用户名格式
	 *
	 * @param username 待校验参数
	 * @return
	 */
	public static boolean maybeUsername(String username) {
		if (!username.matches(USERNAME_PATTERN)) {
			return false;
		}
		// 如果用户名不在指定范围内也是错误的
		if (username.length() < USERNAME_MIN_LENGTH || username.length() > USERNAME_MAX_LENGTH) {
			return false;
		}
		return true;
	}

	/**
	 * 校验是否匹配邮箱格式
	 *
	 * @param username 待校验参数
	 * @return
	 */
	public static boolean maybeEmail(String username) {
		if (!username.matches(EMAIL_PATTERN)) {
			return false;
		}
		return true;
	}

	/**
	 * 校验是否匹配手机号格式
	 *
	 * @param username 待校验参数
	 * @return
	 */
	public static boolean maybeMobilePhoneNumber(String username) {
		if (!username.matches(MOBILE_PHONE_NUMBER_PATTERN)) {
			return false;
		}
		return true;
	}

	public enum AuthcType {
		bcrypt(new AuthcEncryptor() {
		}),
		md5(new AuthcEncryptor() {
			@Override
			public String encrypt(String password, String salt) {
				return Hashes.md5(password, salt);
			}

			@Override
			public boolean matches(String tryPassword, String salt, String password) {
				return encrypt(tryPassword, salt).equals(password);
			}
		}),
		sha1(new AuthcEncryptor() {
			@Override
			public String encrypt(String password, String salt) {
				return Hashes.sha1(password, salt);
			}

			@Override
			public boolean matches(String tryPassword, String salt, String password) {
				return encrypt(tryPassword, salt).equals(password);
			}
		}),
		sha256(new AuthcEncryptor() {
			@Override
			public String encrypt(String password, String salt) {
				return Hashes.sha256(password, salt);
			}

			@Override
			public boolean matches(String tryPassword, String salt, String password) {
				return encrypt(tryPassword, salt).equals(password);
			}
		});
		private AuthcEncryptor encryptor;

		AuthcType(AuthcEncryptor encryptor) {
			this.encryptor = encryptor;
		}

		public AuthcEncryptor getEncryptor() {
			return encryptor;
		}
	}

	public interface AuthcEncryptor {
		/**
		 * 使用指定盐值对密码加密
		 *
		 * @param password 明文
		 * @param salt     加密盐
		 * @return 加密后的密文
		 */
		default String encrypt(String password, String salt) {
			// 换用 BCrypt 淘汰 md5
			return BCrypt.hashpw(password + salt, BCrypt.gensalt(10));
		}

		/**
		 * 在对象{@linkplain AuthcToken}上生成盐值与密码密文
		 *
		 * @param authc    用户认证对象
		 * @param password 密码明文
		 * @return
		 */
		default <T extends AuthcToken> T encrypt(T authc, String password) {
			String salt = authc.getSalt();
			if (StringUtils.isBlank(salt)) {
				authc.setSalt(salt = RandomStringUtils.randomAlphanumeric(10));
				//authc.setSalt(salt = new SecureRandomNumberGenerator().nextBytes().toHex());
			}
			authc.setPassword(encrypt(password, salt));
			return authc;
		}

		/**
		 * 校验参数提供的密码的正确性
		 *
		 * @param tryPassword 待校验密码值
		 * @param salt        密码盐值
		 * @param password    密码密文
		 * @return
		 */
		default boolean matches(String tryPassword, String salt, String password) {
			// 换用 BCrypt 淘汰 md5
			return BCrypt.checkpw(tryPassword + salt, password);
		}

		/**
		 * 校验密码
		 *
		 * @param authc       用户认证对象, 内含密码密文及其盐值
		 * @param tryPassword 待校验密码值
		 * @return
		 */
		default boolean matches(AuthcToken authc, String tryPassword) {
			if (!authc.getAuthcChannel().authcness()) {
				return true;
			}
			return matches(tryPassword, authc.getSalt(), authc.getPassword());
		}
	}

	public interface AuthcToken {

		AuthcChannel getAuthcChannel();

		void setAuthcChannel(AuthcChannel authcChannel);

		String getSalt();

		void setSalt(String salt);

		String getPassword();

		void setPassword(String password);
	}

	@Getter
	@Setter
	public static class AuthcTokenImpl implements AuthcToken {
		private AuthcChannel authcChannel = AuthcChannel.DEFAULT;// 认证方式
		private String password;// 密码
		private String salt;// 加密盐
	}
}

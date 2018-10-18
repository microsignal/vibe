package io.microvibe.booster.system.service;

import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.core.accessor.CacheAccessor;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.ReplyCode;
import io.microvibe.booster.core.api.tools.DataKit;
import io.microvibe.booster.core.base.shiro.authc.AuthcChannel;
import io.microvibe.booster.core.base.shiro.authc.AuthcKit;
import io.microvibe.booster.core.base.shiro.authc.AuthcKit.AuthcToken;
import io.microvibe.booster.core.base.shiro.authc.AuthcSessionPacket;
import io.microvibe.booster.core.base.shiro.authc.token.AuthcChannelToken;
import io.microvibe.booster.core.base.shiro.authc.token.LocalAuthcToken;
import io.microvibe.booster.core.log.Logging;
import io.microvibe.booster.system.entity.SysUser;
import io.microvibe.booster.system.entity.SysUserAuthc;
import io.microvibe.booster.system.err.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class SysAuthService {

	private Cache passwordRetryCache;

	@Value(value = "${system.user.password.maxRetryCount:10}")
	private int maxRetryCount = 10;
	@Autowired
	private SysUserAuthcService sysUserAuthcService;
	@Autowired
	private SysUserService sysUserService;

	@PostConstruct
	public void init() {
		passwordRetryCache = CacheAccessor.getPasswordRetryCache();
	}

	/**
	 * 尝试登录,成功则返回会话对象,失败时抛出异常
	 *
	 * @param token
	 * @return
	 */
	@org.springframework.transaction.annotation.Transactional
	public AuthcSessionPacket<Long> login(AuthcChannelToken token) {
		List<AuthcChannel> authcChannels = token.getAuthcChannels();
		if (authcChannels.size() == 0) {
			throw new UnknownAccountException();
		}
		RuntimeException err = null;
		for (AuthcChannel authcChannel : authcChannels) {
			try {
				AuthcSessionPacket<Long> principal = null;
				String authcCode = token.getUsername();
				String password = token.getPassword() == null ? null : new String(token.getPassword());

				if (StringUtils.isEmpty(authcCode) || StringUtils.isEmpty(password)) {
					Logging.logUserInfo(
						authcCode, "loginError", "username is empty");
					throw new UserNotExistsException();
				}
				SysUser sysUser = null;
				SysUserAuthc sysUserAuthc = null;
				switch (authcChannel) {
					case local:
						if (AuthcKit.maybeUsername(authcCode)) {
							sysUser = sysUserService.getByUserName(authcCode);
						}
						if (sysUser == null && AuthcKit.maybeMobilePhoneNumber(authcCode)) {
							sysUser = sysUserService.getByMobilePhone(authcCode);
						}
						if (sysUser == null && AuthcKit.maybeEmail(authcCode)) {
							sysUser = sysUserService.getByEmail(authcCode);
						}
						if (sysUser == null) {
							throw new UserNotExistsException();
						} else {
							sysUserAuthc = sysUserAuthcService.getByAuthcChannelAndUserId(authcChannel, sysUser.getId());
						}
						break;
					case mobile:
						if (!AuthcKit.maybeMobilePhoneNumber(authcCode)) {
							throw new UserNotExistsException();
						}
						// 暂不支持手机号动态口令登录
						break;
					case email:
						if (!AuthcKit.maybeEmail(authcCode)) {
							throw new UserNotExistsException();
						}
						// 暂不支持邮箱动态口令登录
						break;
					default:
				}

				if (sysUserAuthc == null /*|| Boolean.TRUE.equals(sysUserAuthc.getDeleted())*/) {
					throw new UserNotExistsException();
				}

				if (sysUser == null || Boolean.TRUE.equals(sysUser.getDeleted())) {
					throw new UserNotExistsException();
				}
//				if (sysUser.getStatus() == UserStatus.blocked) {
//					Logging.logUserInfo(authcCode, "loginError", "user is blocked!");
//					throw new UserBlockedException("");// userStatusHistoryService.getLastReason(user)
//				}
				if (Boolean.TRUE.equals(sysUser.getLocked())) {
					throw new UserException("用户被锁定!");
				}

				// 验证密码
				validate(sysUserAuthc, password);

				principal = new AuthcSessionPacket(sysUser.getId(), authcChannel, authcCode);

				/*// 验证是否具有登录角色
				if (!hasLoginRole(token.getPlatId(), sysUser.getId())) {
					throw new UserException("用户在该平台没有登录权限!");
				}*/

				// 登录成功
				Logging.logUserInfo(authcCode, "loginSuccess", "");

				return principal;
			} catch (UserException e) {
				err = e;
			} catch (RuntimeException e) {
				log.error(e.getMessage(), e);
				err = e;
			}
		}
		if (err != null) {
			throw err;
		}
		throw new AccountException();
	}

	/**
	 * 按口令顺序依次尝试登录,返回第一个成功登录后的信息
	 *
	 * @param tokens 登录口令
	 * @return
	 */
	public AuthcSessionPacket<Long> login(Collection<AuthcChannelToken> tokens) {
		if (tokens.size() == 0) {
			throw new UnknownAccountException();
		}
		RuntimeException err = null;
		for (AuthcChannelToken token : tokens) {
			try {
				AuthcSessionPacket<Long> principal = login(token);
				if (principal != null) {
					return principal;
				}
			} catch (RuntimeException e) {
				err = e;
			}
		}
		if (err != null) {
			throw err;
		}
		throw new AccountException();
	}

	public AuthcSessionPacket<Long> login(AuthcChannelToken... tokens) {
		return login(Arrays.asList(tokens));
	}

	/**
	 * 尝试登录,成功则返回会话对象,失败时抛出异常
	 *
	 * @param token
	 * @return
	 */
	public AuthcSessionPacket<Long> login(AuthenticationToken token) {
		if (token instanceof AuthcChannelToken) {
			AuthcChannelToken authcChannelToken = (AuthcChannelToken) token;
			//return ((SysAuthService) AopContext.currentProxy()).login(authcChannelToken);
			return login(authcChannelToken);
		} else if (token instanceof UsernamePasswordToken) {
			UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
			String password = usernamePasswordToken.getPassword() == null ? null
				: new String(usernamePasswordToken.getPassword());
			String username = usernamePasswordToken.getUsername();
			boolean rememberMe = usernamePasswordToken.isRememberMe();
			return login(new LocalAuthcToken(username, password, rememberMe));
		} else {
			// 不支持其他认证方式
			throw new ApiException(ReplyCode.TxnAuthChannelUnsupported);
		}
	}

	/**
	 * 验证指定认证方式下的密码有效性
	 *
	 * @param authc
	 * @param password
	 */
	public void validate(SysUserAuthc authc, String password) throws SystemException {
		// FIXME 目前只支持常规用户密码校验, 考虑对于手机号、邮箱等认证使用一次性动态口令的方式, 需要加入密码期限的限制
		String userId = authc.getUserId().toString();
		AtomicInteger retryCount = new AtomicInteger(0);
		Cache.ValueWrapper cacheElement = passwordRetryCache.get(userId);
		if (cacheElement != null) {
			retryCount = (AtomicInteger) cacheElement.get();
		}
		if (retryCount != null) {
			if (retryCount.get() >= maxRetryCount) {
				Logging.logUserInfo(
					userId, "passwordError", "password error, retry limit exceed! password: {},max retry count {}",
					password, maxRetryCount);
				throw new UserPasswordRetryLimitExceedException(maxRetryCount);
			}
		} else {
			retryCount = new AtomicInteger(0);
		}

		if (!matches(authc, password)) {
			retryCount.incrementAndGet();
			passwordRetryCache.put(userId, retryCount);
			Logging.logUserInfo(
				userId, "passwordError", "password error! password: {} retry count: {}",
				password, retryCount);
			throw new UserPasswordNotMatchException();
		} else {
			passwordRetryCache.evict(userId);
		}
	}

	/**
	 * @see AuthcKit#encrypt(AuthcToken, String)
	 */
	private void encrypt(SysUserAuthc authc, String password) {
		// 兼容老系统数据,使用md5
		AuthcKit.useAuthcType(AuthcKit.AuthcType.md5);
		try {
			//AuthcKit.AuthcToken payload = new AuthcKit.AuthcTokenImpl();
			String account = StringUtils.trimToEmpty(authc.getAccount());
			String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
			authc.setSalt(salt);
			String encrypt = AuthcKit.encrypt(password, account + salt);
			authc.setPassword(encrypt);
		} finally {
			AuthcKit.clearAuthcType();
		}
	}

	/**
	 * @see AuthcKit#matches(AuthcToken, String)
	 */
	private boolean matches(SysUserAuthc authc, String tryPassword) {
		// 兼容老系统数据,使用md5
		AuthcKit.useAuthcType(AuthcKit.AuthcType.md5);
		try {
			String password = StringUtils.trimToEmpty(authc.getPassword());
			String salt = StringUtils.trimToEmpty(authc.getSalt());
			String account = StringUtils.trimToEmpty(authc.getAccount());
			return AuthcKit.matches(tryPassword, account + salt, password);
		} finally {
			AuthcKit.clearAuthcType();
		}
	}

	// 添加用户认证方式
	public void addUserAuthc(SysUser user, AuthcChannel authcChannel, String password) {
		if (authcChannel.authcness()) {
			// 需要作认证,不能缺少密码
			ReplyCode.RequestParamError.assertNotEmpty("用户密码不能为空", password);
		} else {
			// 对于不需要作认证的登录方式, 忽略密码
			password = "";
		}
		SysUserAuthc localAuthc = new SysUserAuthc();
		localAuthc.setUserId(user.getId());
		localAuthc.setAccount(user.getUsername());
		localAuthc.setAuthcChannel(authcChannel);
		encrypt(localAuthc, password);
		sysUserAuthcService.insert(localAuthc);
	}

	/**
	 * 注册新用户
	 *
	 * @param user     用户对象
	 * @param password 用户密码
	 * @return
	 */
	public SysUser register(SysUser user, String password) {
		// 参数校验
		DataKit.validate(user);
		ReplyCode.RequestParamError.assertNotEmpty("用户密码不能为空", password);

		// 保存用户
		sysUserService.saveOrUpdate(user);

		// 保存认证: username + password
		if (StringUtils.isNotEmpty(user.getUsername())) {
			SysUserAuthc localAuthc = new SysUserAuthc();
			localAuthc.setUserId(user.getId());
			localAuthc.setAccount(user.getUsername());
			localAuthc.setAuthcChannel(AuthcChannel.local);
			encrypt(localAuthc, password);
			sysUserAuthcService.saveOrUpdate(localAuthc);
		}
		/* mobile/email 使用动态口令方式
		// 保存认证: mobile + password
		if (StringUtils.isNotEmpty(user.getMobilePhone())) {
			SysUserAuthc localAuthc = new SysUserAuthc();
			localAuthc.setUserId(user.getId());
			localAuthc.setAuthcChannel(AuthcChannel.mobile);
			encrypt(localAuthc, password);
			sysUserAuthcService.saveOrUpdate(localAuthc);
		}
		// 保存认证: email + password
		if (StringUtils.isNotEmpty(user.getEmail())) {
			SysUserAuthc localAuthc = new SysUserAuthc();
			localAuthc.setUserId(user.getId());
			localAuthc.setAuthcChannel(AuthcChannel.email);
			encrypt(localAuthc, password);
			sysUserAuthcService.saveOrUpdate(localAuthc);
		}*/
		return user;
	}
}

package io.microvibe.booster.core.env;

import lombok.Getter;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Getter
public class ShiroEnv {
	@Value("${shiro.session.globalSessionTimeout:10800000}")
	long globalSessionTimeout;// 全局session超时时间 1000*60*60*3

	@Value("${shiro.session.validation.interval:1800000}")
	long sessionValidationInterval;// session验证时间间隔(即验证会话是否还有效)

	@Value("${shiro.active.session.cacheName:shiro-session}")
	String sessionCacheName;// 缓存的名字
	@Value("${shiro.authc.cacheName:shiro-authc}")
	String authenticationCacheName;// AuthenticationCacheName
	@Value("${shiro.authz.cacheName:shiro-authz}")
	String authorizationCacheName;// AuthorizationCacheName

	@Value("${shiro.uid.cookie.name:uid}")
	String uidCookieName;

	@Value("${shiro.uid.cookie.domain:}")
	String uidCookieDomain;
	@Value("${shiro.uid.cookie.path:/}")
	String uidCookiePath;
	@Value("${shiro.uid.cookie.httpOnly:true}")
	boolean uidCookieHttpOnly;
	@Value("${shiro.uid.cookie.maxAge:-1}")
	int uidCookieMaxAge;

	@Value("${shiro.uid.rememeberMe.cookie.name:rememberMe}")
	String rememeberMeCookieName;
	@Value("${shiro.uid.rememeberMe.cookie.domain:}")
	String rememeberMeCookieDomain;
	@Value("${shiro.uid.rememeberMe.cookie.path:/}")
	String rememeberMeCookiePath;
	@Value("${shiro.uid.rememeberMe.cookie.httpOnly:true}")
	boolean rememeberMeCookieHttpOnly;
	@Value("${shiro.uid.rememeberMe.cookie.maxAge:2592000}")
	int rememeberMeCookieMaxAge;// 30days
	/*
	 * 密钥生成java代码（直接拷贝到main运行即可）
	 *  KeyGenerator keygen = KeyGenerator.getInstance("AES");
	 *  SecretKey deskey = keygen.generateKey();
	 *  System.out.println(Base64.encodeToString(deskey.getEncoded()));
	 */
	@Value("${shiro.uid.rememeberMe.cookie.base64.cipherKey:4AvVhmFLUs0KTA3Kprsdag==}")
	String rememeberMeCookieCipherKey;// rememberme cookie加密的密钥

	@Value("${shiro.login.url:/login}")
	String loginUrl;
	@Value("${shiro.logout.success.url:/login?logout=1}")
	String logoutSuccessUrl;
	@Value("${shiro.user.notfound.url:/login?notfound=1}")
	String userNotfoundUrl;
	@Value("${shiro.user.blocked.url:/login?blocked=1}")
	String userBlockedUrl;
	@Value("${shiro.user.unknown.error.url:/login?unknown=1}")
	String userUnknownErrorUrl;
	@Value("${shiro.user.force.logout.url:/login?forcelogout=1}")
	String userForceLogoutUrl;
	@Value("${shiro.unauthorizedUrl:/unauthorized}")
	String unauthorizedUrl;
	@Value("${shiro.default.success.url:/}")
	String defaultSuccessUrl;
	@Value("${shiro.admin.default.success.url:/admin/index}")
	String adminSuccessUrl;
	@Value("${shiro.jcaptcha.enable:true}")
	boolean jcaptchaEnable;
	@Value("${shiro.jcaptcha.error.url:/login?jcaptchaError=1}")
	String jcaptchaErrorUrl;


	@Value("#{'${shiro.sessionDAO.service:}'?:''}")
	private String onlineSessionDAOService;
	@Value("#{'${shiro.authc.service:}'?:''}")
	private String shiroAuthcServiceClass;
	@Value("#{'${shiro.authc.param.username:}'?:'username'}")
	private String usernameParam = FormAuthenticationFilter.DEFAULT_USERNAME_PARAM;
	@Value("#{'${shiro.authc.param.password:}'?:'password'}")
	private String passwordParam = FormAuthenticationFilter.DEFAULT_PASSWORD_PARAM;
	@Value("#{'${shiro.authc.param.rememberMe:}'?:'rememberMe'}")
	private String rememberMeParam = FormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM;
	@Value("#{'${shiro.authc.param.shiroLoginFailure:}'?:'shiroLoginFailure'}")
	private String failureKeyAttribute = FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME;

	private Map<String, String> filterMap = new LinkedHashMap<>();

	@Value("#{'${shiro.filter.map:}'?:''}")
	public void setFilterMap(String filterMapStr) {
		try {
			StringReader reader = new StringReader(filterMapStr);
			BufferedReader br = new BufferedReader(reader);
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				String[] groups = line.split("[;]");
				for(String group : groups){
					String[] arr = group.split("=");
					if (arr.length < 2) continue;
					filterMap.put(arr[0].trim(), arr[1].trim());
				}

			}
		} catch (IOException e) {
		}
	}

}

package io.microvibe.booster.core.api;

import io.microvibe.booster.commons.spring.MessageResources;
import io.microvibe.booster.commons.utils.StringUtils;

import java.util.regex.Pattern;

public enum ReplyCode {
	// 成功
	Success("R0000", "操作成功"), //

	// 系统错误
	Error("R0001", "操作失败"), //
	SysLogicError("R0002", "系统逻辑异常"), //

	// 数据校验类错误
	RequestParseError("R1000", "接口报文数据解析失败"), //
	RequestHeadError("R1001", "接口报文头格式有误"), //
	RequestHeadEmpty("R1002", "接口报文头不能为空"), //
	RequestBodyError("R1003", "接口报文体格式有误"), //
	RequestBodyEmpty("R1004", "接口报文体不能为空"), //
	RequestCodeEmpty("R1005", "接口代码不能为空"), //
	RequestCodeUnsupported("R1006", "接口代码不支持"), //
	RequestParamError("R1007", "参数校验失败"),

	RequestAppIdError("R1010", "appId无效"), //
	RequestAppIdEmpty("R1011", "appId不能为空"), //
	RequestAppSecretError("R1012", "appSecret无效"), //
	RequestAppSecretEmpty("R1013", "appSecret不能为空"), //
	RequestTokenError("R1014", "AccessToken无效"), //
	RequestTokenEmpty("R1015", "AccessToken不能为空"), //
	RequestTokenGenError("R1016", "AccessToken生成失败"), //
	RequestCaptchaInvalid("R1017", "验证码无效"), //
	RequestLoginTokenInvalid("R1018", "登录令牌无效"), //
	RequestPasswordError("R1019", "密码错误"),
	RequestPageParamEmpty("R1020", "查询分页条件不能为空"), //

	// 权限限制类错误
	TxnUnauthorizedError("R2000", "权限不足"), //
	TxnSessionUnauthenticated("R2001", "当前会话未认证"), //
	TxnAuthChannelUnsupported("R2002", "认证方式不支持"), //
	TxnSessionKickedOut("R2003", "当前用户被强制退出"), //
	TxnSessionUserDeleted("R2004", "当前用户被删除"), //
	TxnSessionUserBlocked("R2005", "当前用户被封禁"), //

	// 逻辑处理类错误
	TxnProcessError("R3000", "处理错误"), //
	TxnUnimplemented("R3001", "功能尚未实现"), //
	TxnUnavailable("R3002", "功能暂不可用"), //
	TxnDataNotExist("R3003", "数据信息不存在"), //
	TxnDataAlreadyExist("R3004", "数据信息已存在"), //
	TxnDataUncompleted("R3005", "数据信息不完整"), //

	// 消息响应类错误
	ResponseFormatError("R4001", "响应数据编码失败"), //

	;

	private String code;
	private String message;

	private ReplyCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	@Override
	public String toString() {
		return code;
	}

	public int getCodeAsInt() {
		return Integer.parseInt(code.replaceFirst("^[^\\d]+", ""));
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return MessageResources.getMessageOrDefault(code, message);
	}

	public void fail(String message) {
		throw new ApiException(this, message);
	}

	public void fail() {
		fail(null);
	}

	public void assertPattern(String message, Pattern pattern, String str) {
		if (!pattern.matcher(str).matches()) {
			fail(message);
		}
	}

	public void assertPattern(String message, String pattern, String str) {
		if (!Pattern.compile(pattern).matcher(str).matches()) {
			fail(message);
		}
	}

	public void assertTrue(String message, boolean expression) {
		if (!expression) {
			fail(message);
		}
	}

	public void assertNull(String message, Object object) {
		if (object != null) {
			fail(message);
		}
	}

	public void assertEmpty(String message, String str) {
		if (str != null && !"".equals(str)) {
			fail(message);
		}
	}

	public void assertBlank(String message, String str) {
		if (StringUtils.isNotBlank(str)) {
			fail(message);
		}
	}

	public void assertEquals(String message, Object o1, Object o2) {
		if (!o1.equals(o2)) {
			fail(message);
		}
	}

	public void assertNotTrue(String message, boolean expression) {
		if (expression) {
			fail(message);
		}
	}

	public void assertNotNull(String message, Object object) {
		if (object == null) {
			fail(message);
		}
	}

	public void assertNotEmpty(String message, String str) {
		if (str == null || "".equals(str)) {
			fail(message);
		}
	}

	public void assertNotBlank(String message, String str) {
		if (StringUtils.isBlank(str)) {
			fail(message);
		}
	}

	public void assertNotEquals(String message, Object o1, Object o2) {
		if (o1.equals(o2)) {
			fail(message);
		}
	}
}

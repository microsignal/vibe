package io.microvibe.booster.core.base.web.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.microvibe.booster.commons.utils.json.JsonUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

public class JWTModel {
	public static class Header {
		@JsonProperty
		private String alg = "HS256";
		@JsonProperty
		private String typ = "JWT";
	}

	public static class Payload {
		//		private String iss;//该JWT的签发者
//		private String sub;//该JWT所面向的用户
//		private String aud;//接收该JWT的一方
		private long iat;// (issued at): 签发时间,Unix时间戳
		private long exp;// (expires): 过期时间,Unix时间戳
		@JsonIgnore
		private Serializable inf;// 会话信息

		public long getExp() {
			return exp;
		}

		public long getIat() {
			return iat;
		}

		public Serializable getInf() {
			return inf;
		}

		@JsonProperty("inf")
		public void setInfStr(String infStr) {
			this.inf = (Serializable) SerializationUtils.deserialize(Base64.decodeBase64(infStr));
		}

		@JsonProperty("inf")
		public String getInfStr() {
			return Base64.encodeBase64String(SerializationUtils.serialize(inf));
		}

	}

	public static final long DEFAULT_EXPIRES = 30 * 60 * 1000;// 30min
	@JsonProperty
	private Header header;// 头部
	@JsonProperty
	private Payload payload;// 载荷

	public JWTModel() {
		this(DEFAULT_EXPIRES);
	}

	public JWTModel(Serializable info) {
		this(DEFAULT_EXPIRES, info);
	}

	public JWTModel(long expires) {
		header = new Header();
		payload = new Payload();
		payload.iat = System.currentTimeMillis();
		payload.exp = payload.iat + expires;
	}

	public JWTModel(long expires, Serializable info) {
		header = new Header();
		payload = new Payload();
		payload.iat = System.currentTimeMillis();
		payload.exp = payload.iat + expires;
		payload.inf = info;
	}

	@JsonIgnore
	public void setExpries(long expires) {
		payload.exp = payload.iat + expires;
	}

	@JsonIgnore
	public long getExpiresTime() {
		return payload.getExp();
	}

	@JsonIgnore
	public long getIssuedTime() {
		return payload.getIat();
	}

	@JsonIgnore
	public Serializable getInfo() {
		return payload.getInf();
	}

	@JsonIgnore
	public void setInfo(Serializable info) {
		payload.inf = info;
	}

	public String toJson() {
		String headerJson = JsonUtil.toJson(header);
		String payloadJson = JsonUtil.toJson(payload);
		JWTContext globalJwtContext = JWTContext.getGlobalRsaJwtContext();
		String sign = globalJwtContext.sign(payloadJson);
		String headerStr = Base64.encodeBase64String(headerJson.getBytes());
		String payloadStr = Base64.encodeBase64String(payloadJson.getBytes());
		StringBuilder sb = new StringBuilder();
		sb.append(headerStr).append(".").append(payloadStr).append(".").append(sign);
		return sb.toString();
	}

	@Override
	public String toString() {
		return toJson();
	}

	public static String toString(JWTModel model) {
		return model.toString();
	}

	public static JWTModel fromString(String str) {
		try {
			String[] arr = str.split("\\.");
			if (arr.length < 3) {
				return null;
			}
			String headerStr = arr[0];
			String payloadStr = arr[1];
			String sign = arr[2];
			String payloadJson = new String(Base64.decodeBase64(payloadStr.getBytes()));
			JWTContext globalJwtContext = JWTContext.getGlobalRsaJwtContext();
			boolean verify = globalJwtContext.verify(payloadJson, sign);
			if (verify == false) {// 签名校验不通过
				return null;
			}
			String headerJson = new String(Base64.decodeBase64(headerStr.getBytes()));
			String json = new StringBuilder()
				.append("{header:").append(headerJson)
				.append(", payload:").append(payloadJson)
				.append("}").toString();
			JWTModel model = JsonUtil.toJavaObject(JWTModel.class, json);
			if (model.getExpiresTime() < System.currentTimeMillis()) {
				return null;// 过期
			}
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}

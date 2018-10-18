package io.microvibe.booster.txn.alipay.support;

import com.alipay.api.AlipayConstants;
import com.alipay.api.AlipayObject;
import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;
import com.alipay.api.internal.util.AlipayHashMap;
import lombok.Data;

import java.util.Map;

/**
 * 通用请求
 *
 * @author Qt
 * @since Aug 28, 2018
 */
@Data
public class CommonAlipayRequest<T extends AlipayResponse> implements AlipayRequest<T> {

	private final Class<T> responseClass;
	private String apiMethodName; // 如 alipay.trade.page.pay
	private String apiVersion = "1.0";
	private AlipayHashMap udfParams; // add user-defined text parameters
	private String bizContent;
	private String terminalType;
	private String terminalInfo;
	private String prodCode;
	private String notifyUrl;
	private String returnUrl;
	private boolean needEncrypt = false;
	private AlipayObject bizModel = null;

	private CommonAlipayRequest(Class<T> responseClass) {
		this.responseClass = responseClass;
	}

	public static CommonAlipayRequest<CommonAlipayResponse> create() {
		return new CommonAlipayRequest<>(CommonAlipayResponse.class);
	}

	public static <T extends AlipayResponse> CommonAlipayRequest<T> create(Class<T> clazz) {
		return new CommonAlipayRequest<>(clazz);
	}

	@Override
	public Map<String, String> getTextParams() {
		AlipayHashMap txtParams = new AlipayHashMap();
		txtParams.put(AlipayConstants.BIZ_CONTENT_KEY, this.bizContent);
		if (udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public void putOtherTextParam(String key, String value) {
		if (this.udfParams == null) {
			this.udfParams = new AlipayHashMap();
		}
		this.udfParams.put(key, value);
	}

	@Override
	public Class<T> getResponseClass() {
		return responseClass;
	}
}

package io.microvibe.booster.txn.alipay;

import io.microvibe.booster.core.api.model.HeadModel;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.core.api.tools.DataKit;
import io.microvibe.booster.core.api.txn.BaseApiService;
import io.microvibe.booster.pay.alipay.config.AlipayAppConfig;
import io.microvibe.booster.pay.alipay.config.AlipayConfig;
import io.microvibe.booster.pay.alipay.service.AlipayService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Qt
 * @since Aug 27, 2018
 */
public abstract class AbstractAlipayApiService extends BaseApiService {
	private static final ThreadLocal<Boolean> innerCall = new ThreadLocal<>();
	@Autowired
	protected AlipayService alipayService;

	/**
	 * @param request 请求报文
	 * @return
	 */
	@Override
	public final ResponseData execute(RequestData request) {
		boolean external = innerCall.get() == null || !innerCall.get().booleanValue();
		try {
			if (external) {
				// 外部调用
				innerCall.set(Boolean.TRUE);

				HeadModel head = request.getHead();

				// region alipayConfig
				String alipayRoute = StringUtils.trimToNull(head.getString("alipayRoute"));
				String alipayAuthAppId = StringUtils.trimToNull(head.getString("alipayAuthAppId"));
				if (alipayRoute != null) {
					// 使用指定渠道的 alipayConfig
					AlipayConfig.use(alipayRoute);
				}
				if (alipayAuthAppId != null) {
					// 使用指定的 APP 授权令牌
					AlipayAppConfig.useAuthApp(alipayAuthAppId);
				}
			}
			// endregion
			return doExecute(request);
		} finally {
			if (external) {
				// 清理
				AlipayConfig.clear();
				AlipayAppConfig.clear();
				innerCall.remove();
			}
		}
	}

	public ResponseData doExecute(RequestData request) {
		return DataKit.buildSuccessResponse();
	}

}

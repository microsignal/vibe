package io.microvibe.booster.pay.alipay.service;

import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.core.base.mybatis.mapping.BaseMapper;
import io.microvibe.booster.pay.alipay.entity.PayAlipayAppConfig;
import io.microvibe.booster.pay.alipay.mapper.PayAlipayAppConfigMapper;
import io.microvibe.booster.pay.service.BasePayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Qt
 * @since Aug 30, 2018
 */
@Slf4j
@Service
public class PayAlipayAppConfigService extends BasePayService<PayAlipayAppConfig, Long> {

	@Autowired
	@BaseComponent
	private PayAlipayAppConfigMapper mapper;

	@Override
	public BaseMapper<PayAlipayAppConfig, Long> getMapper() {
		return mapper;
	}

	/**
	 * 通过配置标识符查询令牌
	 *
	 * @param identity
	 * @return
	 */
	public PayAlipayAppConfig getByIdentity(String identity) {
		PayAlipayAppConfig param = new PayAlipayAppConfig();
		param.setIdentity(identity);
		return getUnique(param);
	}

	/**
	 * 通过主配置ID 与 授权AppID查询令牌
	 *
	 * @param configId
	 * @param authAppId
	 * @return
	 */
	public PayAlipayAppConfig getByConfigIdAndAppId(Long configId, String authAppId) {
		if (configId == null || authAppId == null) {
			return null;
		}
		PayAlipayAppConfig param = new PayAlipayAppConfig();
		param.setAlipayConfigId(configId);
		param.setAuthAppId(authAppId);
		param.setValid(true);
		return getUnique(param);
	}

	/**
	 * 通过主配置ID 与 授权UserID查询令牌
	 *
	 * @param configId
	 * @param authUserId
	 * @return
	 */
	public PayAlipayAppConfig getByConfigIdAndUserId(Long configId, String authUserId) {
		if (configId == null || authUserId == null) {
			return null;
		}
		PayAlipayAppConfig param = new PayAlipayAppConfig();
		param.setAlipayConfigId(configId);
		param.setAuthUserId(authUserId);
		param.setValid(true);
		return getUnique(param);
	}


}

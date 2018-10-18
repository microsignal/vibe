package io.microvibe.booster.pay.alipay.service;

import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.core.base.mybatis.mapping.BaseMapper;
import io.microvibe.booster.pay.alipay.entity.PayAlipayConfig;
import io.microvibe.booster.pay.alipay.mapper.PayAlipayConfigMapper;
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
public class PayAlipayConfigService extends BasePayService<PayAlipayConfig, Long> {

	@Autowired
	@BaseComponent
	private PayAlipayConfigMapper mapper;

	@Override
	public BaseMapper<PayAlipayConfig, Long> getMapper() {
		return mapper;
	}

	/**
	 * 通过配置标识符查询令牌
	 *
	 * @param identity
	 * @return
	 */
	public PayAlipayConfig getByIdentity(String identity) {
		PayAlipayConfig param = new PayAlipayConfig();
		param.setIdentity(identity);
		return getUnique(param);
	}


}

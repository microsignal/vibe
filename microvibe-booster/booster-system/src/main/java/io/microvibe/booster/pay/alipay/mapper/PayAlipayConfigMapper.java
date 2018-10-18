package io.microvibe.booster.pay.alipay.mapper;

import io.microvibe.booster.pay.alipay.entity.PayAlipayConfig;
import io.microvibe.booster.pay.mapper.BasePayMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author Qt
 * @since Aug 30, 2018
 */
@Mapper
@Repository
public interface PayAlipayConfigMapper extends BasePayMapper<PayAlipayConfig> {

}

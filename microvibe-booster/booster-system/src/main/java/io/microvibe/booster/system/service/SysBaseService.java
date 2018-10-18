package io.microvibe.booster.system.service;

import io.microvibe.booster.core.base.entity.BaseEntity;
import io.microvibe.booster.core.base.service.MybatisBaseService;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
public abstract class SysBaseService<M extends BaseEntity<ID>, ID extends Serializable>
	extends MybatisBaseService<M, ID> {

//	/**
//	 * 公共展示类
//	 *
//	 * @param t     实体
//	 * @param page  页
//	 * @param limit 行
//	 * @return
//	 */
//	public String show(M t, int page, int limit) {
//		List<M> tList = null;
//		Page<M> tPage = PageHelper.startPage(page, limit);
//		try {
//			tList = baseMapper.selectByEntity(t);
//		} catch (Exception e) {
//			log.error("class:BaseServiceImpl ->method:show->message:" + e.getMessage());
//			e.printStackTrace();
//		}
//		ReType reType = new ReType(tPage.getTotal(), tList);
//		return JSON.toJSONString(reType);
//	}














}

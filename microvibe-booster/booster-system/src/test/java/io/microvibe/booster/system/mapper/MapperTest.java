package io.microvibe.booster.system.mapper;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.system.entity.A;
import io.microvibe.booster.system.entity.SysDict;
import io.microvibe.booster.system.service.SysDictService;
import io.microvibe.booster.test.BaseTestCase;
import org.junit.Test;

import java.util.List;

/**
 * @author Qt
 * @since Aug 25, 2018
 */
public class MapperTest  extends BaseTestCase {

	@Test
	public void test001() {
		EntityATestMapper mapper = ApplicationContextHolder.getBean(EntityATestMapper.class);

		A a = mapper.getById(1L);
		System.out.println(a);
	}
}

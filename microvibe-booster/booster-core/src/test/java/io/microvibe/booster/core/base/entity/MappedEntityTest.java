package io.microvibe.booster.core.base.entity;

import com.alibaba.fastjson.JSON;
import io.microvibe.booster.commons.utils.json.JsonUtil;
import lombok.Data;
import lombok.experimental.Delegate;
import org.junit.Test;

/**
 * @author Qt
 * @since May 28, 2018
 */
public class MappedEntityTest {

	@Test
	public void test() {
		A entity = MappedEntity.newInstance(A.class);
		entity.put("sex", "1");
		entity.setName("testname");
		entity.setSuccessful(true);
		System.out.println(entity.get("name"));
		System.out.println(entity.get("sex"));
		System.out.println(entity.get("intro"));
		System.out.println(entity.getSex());

		System.out.println(entity);
		System.out.println(JsonUtil.toJson(entity));
		System.out.println(JSON.toJSONString(entity));

	}

	@Data
	static class A extends MappedEntity {

		String name;
		String sex;
		boolean successful;

//		public static class Wrapper extends A {
//			@Delegate
//			A delegate = MappedEntity.newInstance(A.class);
//		}
	}
}

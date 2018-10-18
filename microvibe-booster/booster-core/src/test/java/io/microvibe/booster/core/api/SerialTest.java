package io.microvibe.booster.core.api;

import com.alibaba.fastjson.JSONObject;
import io.microvibe.booster.commons.cache.impl.Serializers;
import io.microvibe.booster.commons.utils.BeanUtils;
import io.microvibe.booster.commons.utils.json.JsonUtil;
import io.microvibe.booster.commons.utils.serial.Hessians;
import io.microvibe.booster.commons.utils.serial.SerializationUtils;
import io.microvibe.booster.core.base.shiro.authc.AuthcChannel;
import io.microvibe.booster.core.base.shiro.authc.AuthcSessionPacket;
import org.apache.shiro.session.mgt.SimpleSession;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Qt
 * @since Jul 07, 2018
 */
public class SerialTest {

	@Test
	public void test1() throws IOException {
		SimpleSession session = new SimpleSession();
		session.setId("test");

		String str = Hessians.toString(session);
		System.out.println(str);
		Object o = Hessians.toObject(str);
		System.out.println(JsonUtil.toJson(o));
		SerializationUtils.type = SerializationUtils.Type.jdk;
		byte[] serialize = SerializationUtils.serialize(session);
		final Object deserialize = SerializationUtils.deserialize(serialize);
		System.out.println(JsonUtil.toJson(deserialize));

	}

	@Test
	public void test2() throws IOException {
		AuthcSessionPacket packet = new AuthcSessionPacket();
		packet.setUserId("test");
		packet.setAuthcChannel(AuthcChannel.DEFAULT);
		packet.setAuthcCode("name");
		packet.setAttribute("k", "v");

		AuthcSessionPacket packet2 = new AuthcSessionPacket();
		BeanUtils.copyProperties(packet, packet2);

		System.out.println(JSONObject.toJSONString(packet2,true));

	}
}

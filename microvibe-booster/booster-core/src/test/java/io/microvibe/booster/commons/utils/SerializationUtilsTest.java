package io.microvibe.booster.commons.utils;

import io.microvibe.booster.commons.cache.impl.Serializers;
import io.microvibe.booster.commons.utils.serial.Kryos;
import lombok.Data;

/**
 * @author Qt
 * @since Jul 07, 2018
 */
public class SerializationUtilsTest {


	@Data
	public static class T {
		private static final long serialVersionUID = 4L;
		private String name;
		private String id;
		private T2 t2 = new T2();
		{
			t2.f1 = "f1";
		}
	}
	@Data
	public static class T2{
		private String f1;
		private String f2;
	}
	public static void main(String[] args) {
		T t = new T();
		t.setId("test");
//		t.setName("test");
		Object object =  new Object[]{t,t};
//		{
//			String str = JsonUtil.toJson(object);
//			System.out.println(str);
//
//			/*{
//				long begin = System.currentTimeMillis();
//				for (int i = 0; i < 100000; i++) {
//					JSONObject.parseObject(str,T.class);
//				}
//				System.out.printf("time: %s ms\n",(System.currentTimeMillis()-begin));
//			}
//			{
//				long begin = System.currentTimeMillis();
//				for (int i = 0; i < 100000; i++) {JsonUtil.toJavaObject(T.class, str);
//				}
//				System.out.printf("time: %s ms\n",(System.currentTimeMillis()-begin));
//			}*/
//			System.out.println(JSONObject.parseObject(str,T.class));
//			System.out.println(JsonUtil.toJavaObject(T.class, str));
//		}
		String str = Kryos.toString(object);
		System.out.println(str);
		{
			Object o = Kryos.toObject(str);
			System.out.println(o.getClass().getClassLoader());
			System.out.println(ObjectUtils.toString(o));
		}
		{
			str = "AQBbTGphdmEubGFuZy5PYmplY3S7AQMBAWNvbS5qeHd5LmR6d2wuYm9vc3Rlci5jb21tb25zLnV0aWxzLlNlcmlhbGl6YXRpb25VdGlsc1Rlc3Qk1AEBdGVz9AABAmNvbS5qeHd5LmR6d2wuYm9vc3Rlci5jb21tb25zLnV0aWxzLlNlcmlhbGl6YXRpb25VdGlsc1Rlc3QkVLIBAWaxAAEBAw==";
			Object o = Kryos.toObject(str);
			System.out.println(o.getClass().getClassLoader());
			System.out.println(ObjectUtils.toString(o));
		}
		{
			System.out.println(new String(Serializers.serialize(object)));
			System.out.println(
				ObjectUtils.toString(Serializers.deserialize(Serializers.serialize(object))));
		}



	}
}

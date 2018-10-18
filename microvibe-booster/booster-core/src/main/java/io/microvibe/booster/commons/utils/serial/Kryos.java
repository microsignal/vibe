package io.microvibe.booster.commons.utils.serial;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

/**
 * @author Qt
 * @since Jul 07, 2018
 */
public class Kryos {
	private static final ThreadLocal<Kryo> kryoLocal = new ThreadLocal<Kryo>() {
		@Override
		protected Kryo initialValue() {
			Kryo kryo = new Kryo();
			//支持对象循环引用（否则会栈溢出）
			kryo.setReferences(true); //默认值就是 true，添加此行的目的是为了提醒维护者，不要改变这个配置
			//不强制要求注册类（注册行为无法保证多个 JVM 内同一个类的注册编号相同；而且业务系统中大量的 Class 也难以一一注册）
			kryo.setRegistrationRequired(false); //默认值就是 false，添加此行的目的是为了提醒维护者，不要改变这个配置
			//Fix the NPE bug when deserializing Collections.
			((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
				.setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
			return kryo;
		}
	};

	public static Kryo instance() {
		return kryoLocal.get();
	}

	/**
	 * 将对象【及类型】序列化为字节数组
	 *
	 * @param obj 任意对象
	 * @param <T> 对象的类型
	 * @return 序列化后的字节数组
	 */
	public static <T> byte[] toByteArray(T obj) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Output output = new Output(byteArrayOutputStream);

		Kryo kryo = instance();
		kryo.writeClassAndObject(output, obj);
		output.flush();

		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * 将对象【及类型】序列化为 String
	 * 利用了 Base64 编码
	 *
	 * @param obj 任意对象
	 * @param <T> 对象的类型
	 * @return 序列化后的字符串
	 */
	public static <T> String toString(T obj) {
		return Base64.getEncoder().encodeToString(toByteArray(obj));
	}

	/**
	 * 将字节数组反序列化为原对象
	 *
	 * @param byteArray toByteArray 方法序列化后的字节数组
	 * @param <T>       原对象的类型
	 * @return 原对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T toObject(byte[] byteArray) {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
		Input input = new Input(byteArrayInputStream);
		Kryo kryo = instance();
		return (T) kryo.readClassAndObject(input);
	}

	/**
	 * 将 String 反序列化为原对象
	 * 利用了 Base64 编码
	 *
	 * @param str toString 方法序列化后的字符串
	 * @param <T> 原对象的类型
	 * @return 原对象
	 */
	public static <T> T toObject(String str) {
		return toObject(Base64.getDecoder().decode(str.getBytes()));
	}

	/**
	 * 将对象序列化为字节数组
	 *
	 * @param obj 任意对象
	 * @param <T> 对象的类型
	 * @return 序列化后的字节数组
	 */
	public static <T> byte[] toByteArrayTypeless(T obj) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Output output = new Output(byteArrayOutputStream);

		Kryo kryo = instance();
		kryo.writeObject(output, obj);
		output.flush();

		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * 将对象序列化为 String
	 * 利用了 Base64 编码
	 *
	 * @param obj 任意对象
	 * @param <T> 对象的类型
	 * @return 序列化后的字符串
	 */
	public static <T> String toStringTypeless(T obj) {
		return new String(Base64.getEncoder().encodeToString(toByteArrayTypeless(obj)));
	}

	/**
	 * 将字节数组反序列化为原对象
	 *
	 * @param byteArray toByteArray 方法序列化后的字节数组
	 * @param clazz     原对象的 Class
	 * @param <T>       原对象的类型
	 * @return 原对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T toObjectTypeless(byte[] byteArray, Class<T> clazz) {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
		Input input = new Input(byteArrayInputStream);
		Kryo kryo = instance();
		return kryo.readObject(input, clazz);
	}

	/**
	 * 将 String 反序列化为原对象
	 * 利用了 Base64 编码
	 *
	 * @param str   toString 方法序列化后的字符串
	 * @param clazz 原对象的 Class
	 * @param <T>   原对象的类型
	 * @return 原对象
	 */
	public static <T> T toObjectTypeless(String str, Class<T> clazz) {
		return toObjectTypeless(Base64.getDecoder().decode(str.getBytes()), clazz);
	}
}

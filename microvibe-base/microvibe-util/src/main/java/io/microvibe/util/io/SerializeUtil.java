package io.microvibe.util.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import io.microvibe.util.JsonUtil;
import io.microvibe.util.StringUtil;
import io.microvibe.util.err.SerializedException;

/**
 * Java版的 Serialize,拷贝自www.sojson.com
 * @since 2017/09/25
 * @author Qt
 */
@SuppressWarnings("unchecked")
public class SerializeUtil {

	public static void main(String[] args) {
		Object value = "test";
		byte[] rv = serialize(value);
		System.out.println(StringUtil.toHex(rv));
		System.out.println(deserialize(rv));
		//aced000570
		//aced000574000474657374
		//aced000574002274657374207465737420746573742074657374207465737420746573742074657374
		System.out.println(new String(rv));
		System.out.println(JsonUtil.object2Json(value));
	}

	public static byte[] serialize(Object value) throws SerializedException {
		byte[] rv = null;
		ByteArrayOutputStream bos = null;
		ObjectOutputStream os = null;
		try {
			bos = new ByteArrayOutputStream();
			os = new ObjectOutputStream(bos);
			os.writeObject(value);
			rv = bos.toByteArray();
		} catch (Exception e) {
			throw new SerializedException(e);
		} finally {
			close(os);
			close(bos);
		}
		return rv;
	}

	public static Object deserialize(byte[] in) throws SerializedException {
		return deserialize(in, Object.class);
	}

	public static <T> T deserialize(byte[] in, Class<T> type) throws SerializedException {
		Object rv = null;
		ByteArrayInputStream bis = null;
		ObjectInputStream is = null;
		try {
			if (in != null) {
				bis = new ByteArrayInputStream(in);
				is = new ObjectInputStream(bis);
				rv = is.readObject();
			}
		} catch (Exception e) {
			throw new SerializedException(e);
		} finally {
			close(is);
			close(bis);
		}
		return (T) rv;
	}

	private static void close(Closeable closeable) {
		if (closeable != null)
			try {
				closeable.close();
			} catch (IOException e) {
			}
	}

}

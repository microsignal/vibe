package io.microvibe.booster.commons.utils.serial;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * @author Qt
 * @since Jul 07, 2018
 */
public class Hessians {

	public static byte[] toByteArray(Object object) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			Hessian2Output out = new Hessian2Output(bos);
			out.startMessage();
			out.writeObject(object);
			out.completeMessage();
			out.flush();
			return bos.toByteArray();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static String toString(Object obj) {
		return Base64.getEncoder().encodeToString(toByteArray(obj));
	}

	public static Object toObject(String str) {
		return toObject(Base64.getDecoder().decode(str.getBytes()));
	}

	public static Object toObject(byte[] bytes) {
		try {
			Hessian2Input in = new Hessian2Input(new ByteArrayInputStream(bytes));
			in.startMessage();
			Object object = in.readObject();
			in.completeMessage();
			return object;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

}




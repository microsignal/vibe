package io.microvibe.booster.commons.utils.serial;

import java.io.*;

/**
 * @author Qt
 * @since Jul 07, 2018
 */
public class SerializationUtils {

	public static Type type = Type.jdk;

	/**
	 * Serialize the given object to a byte array.
	 *
	 * @param object the object to serialize
	 * @return an array of bytes representing the object in a portable fashion
	 */

	public static byte[] serialize(Object object) {
		if (object == null) {
			return null;
		}
		switch (type) {
			case hessian:
				return Hessians.toByteArray(object);
			case kryo:
				return Kryos.toByteArray(object);
			case jdk:
			default:
				ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
				try {
					ObjectOutputStream oos = new ObjectOutputStream(baos);
					oos.writeObject(object);
					oos.flush();
				} catch (IOException ex) {
					throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), ex);
				}
				return baos.toByteArray();
		}
	}

	/**
	 * Deserialize the byte array into an object.
	 *
	 * @param bytes a serialized object
	 * @return the result of deserializing the bytes
	 */
	public static Object deserialize(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		switch (type) {
			case hessian:
				return Hessians.toObject(bytes);
			case kryo:
				return Kryos.toObject(bytes);
			case jdk:
			default:
				try {
					ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
					return ois.readObject();
				} catch (IOException ex) {
					throw new IllegalArgumentException("Failed to deserialize object", ex);
				} catch (ClassNotFoundException ex) {
					throw new IllegalStateException("Failed to deserialize object type", ex);
				}
		}
	}

	public static enum Type {
		kryo, hessian, jdk
	}

}

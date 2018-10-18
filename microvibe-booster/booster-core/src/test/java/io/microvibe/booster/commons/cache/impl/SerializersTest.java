package io.microvibe.booster.commons.cache.impl;

import org.junit.Ignore;

import java.util.Arrays;

import static io.microvibe.booster.commons.cache.impl.Serializers.deserialize;
import static io.microvibe.booster.commons.cache.impl.Serializers.serialize;

/**
 * @author Qt
 * @since May 28, 2018
 */
@Ignore
public class SerializersTest {

	public static void main(String[] args) {
		System.out.println(Arrays.toString(serialize(null)));
		System.out.println(new String(serialize(null)));
		System.out.println(deserialize(serialize(null)) == null);
	}
}

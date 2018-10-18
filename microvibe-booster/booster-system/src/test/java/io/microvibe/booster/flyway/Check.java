package io.microvibe.booster.flyway;

import io.microvibe.booster.commons.utils.IOUtils;

import java.io.IOException;
import java.util.zip.CRC32;

public class Check {

	public static void main(String[] args) throws IOException {
		byte[] bytes = IOUtils.toByteArray(IOUtils.getInputStream("/db/mysql/V1_0_1__init.sql"));
		System.out.println(calculateChecksum(bytes));
	}

	private static int calculateChecksum(byte[] bytes) {
		final CRC32 crc32 = new CRC32();
		crc32.update(bytes);
		return (int) crc32.getValue();
	}
}

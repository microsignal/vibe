package io.microvibe.util.env;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.apache.commons.codec.binary.Base64;

import io.microvibe.util.crypto.EncryptUtil;

public class PasswordHelper {

	private static String algorithm = "AES";
	private static byte[] key = getKey();

	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			boolean enc = true;
			String text = "";
			if (args.length > 1) {
				text = args[1];
				if ("e".equalsIgnoreCase(args[0])) {
					enc = true;
				} else if ("d".equalsIgnoreCase(args[0])) {
					enc = false;
				}
			} else {
				text = args[0];
			}
			if (enc) {
				System.out.printf("%s%n", encrypt(text));
			} else {
				System.out.printf("%s%n", decrypt(text));
			}
		} else {
			Scanner scanner = new Scanner(System.in);
			loop : while (true) {
				boolean enc = true;
				while (true) {
					System.out.println("选择加密或解密: 1)加密; 0)解密");
					if (scanner.hasNextLine()) {
						String line = scanner.nextLine().trim();
						if (line.equals("1")) {
							enc = true;
							break;
						} else if (line.equals("0")) {
							enc = false;
							break;
						}
					}
				}
				String text = null;
				while (true) {
					System.out.println("输入字符串:");
					if (scanner.hasNextLine()) {
						String line = scanner.nextLine().trim();
						if (!line.equals("")) {
							text = line;
							break;
						}
					}
				}
				if (enc) {
					System.out.printf("加密后的密文为: %s%n", encrypt(text));
				} else {
					System.out.printf("解密后的明文为: %s%n", decrypt(text));
				}

				while (true) {
					System.out.println("是否退出: yes)是; no)否");
					if (scanner.hasNextLine()) {
						String line = scanner.nextLine().trim();
						if (line.equalsIgnoreCase("y") || line.equalsIgnoreCase("yes")
								|| line.equals("1")) {
							break loop;
						} else {
							System.out.println();
							break;
						}
					}
				}
			}
		}
	}
	private static byte[] getKey() {
		try {
			InputStream in = PasswordHelper.class.getResourceAsStream("key.dat");
			byte[] b = new byte[in.available()];
			int off = 0;
			int i = -1;
			while (off < b.length) {
				i = in.read(b, off, b.length - off);
				off += i;
			}
			return b;
		} catch (IOException e) {
			return new byte[0];
		}
	}
	public static String encrypt(String text) throws Exception {
		byte[] rs = EncryptUtil.encrypt(algorithm, key, text.getBytes());
		return new String(Base64.encodeBase64(rs, false), "UTF-8");
	}

	public static String decrypt(String text) throws Exception {
		byte[] rs = EncryptUtil
				.decrypt(algorithm, key, Base64.decodeBase64(text.getBytes("UTF-8")));
		return new String(rs);
	}

}

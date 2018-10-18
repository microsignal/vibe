package io.microvibe.booster.core;

import com.alibaba.druid.filter.config.ConfigTools;
import io.microvibe.booster.commons.crypto.RSA;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Qt
 * @since May 24, 2018
 */
public class DruidTest {


	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Input your password: ");
		while (scanner.hasNextLine()) {
			String password = scanner.nextLine();
			System.out.println("------------------------------------------------------------");
			try {
				System.out.printf("password-plain: %s%n", password);
				ConfigTools.main(new String[]{password});
				break;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("------------------------------------------------------------");
				System.out.println("Input your password: ");
			}
		}
	}

	@Test
	public void dec() throws NoSuchPaddingException, UnsupportedEncodingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
		String password = "ZEf81kcyCFVZwSCZaFTEZ/Rh8HjnbLG1HDhedf+z6hONOYPzJQGVQ2BMHcA6Gfku7Cp1NuMMg3s1a0x36buCGQ==";
		String privateKey = "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAgQcPo/SIrKZpX4N12fogRFgOaJjK5xlvinBjKjAno5JDBNSHdKsqn+UmZOcJKWTBf3ycD49DIzs4TVmtptpwqwIDAQABAkAxKw2svuyuTiPtf90TGZveL2geN8tqUWkN6lH/q6YPUIxd6GxY5R49vgNbpmTyz2JpNLst6M5VhFLGj4gELaBBAiEAwqbWAWh4LbBxVIC1gSKWdPt4f4431EXcXqEjdCjBHTECIQCpsXJve5FdTaIEwjRvuF1IeItLCWNxwIn2KRUMKskEmwIhAL6su1opwf/PpVSHuETFYuk5JPKel+XigVpZMueovHMhAiBPgkAfMiOrNvvKBQCytAypLyn875ozq0Y5G4isGcP0LwIhAJYQHkz7Nl699NfkWNi1T8XUEIWBn3lIyIE5IxHAXY9z";
		String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIEHD6P0iKymaV+Dddn6IERYDmiYyucZb4pwYyowJ6OSQwTUh3SrKp/lJmTnCSlkwX98nA+PQyM7OE1ZrabacKsCAwEAAQ==";
		password = RSA.decrypt(RSA.getPublicKey(publicKey), password);
		System.out.println(password);
	}

	@Test
	public void testMap() {
		Map<String, Object> map = Collections.synchronizedMap(new LinkedHashMap<String, Object>() {
			@Override
			protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
				return this.size() > 10;
			}
		});

		for (int i = 0; i < 10; i++) {
			map.put("key-" + i, "val-" + i);
		}
		System.out.println(map);

		map.put("key-0", 0);
		map.put("key-11", 11);
		System.out.println(map);
	}

}

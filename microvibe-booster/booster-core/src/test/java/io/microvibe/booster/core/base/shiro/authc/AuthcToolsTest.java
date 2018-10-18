package io.microvibe.booster.core.base.shiro.authc;

/**
 * @author Qt
 * @since May 28, 2018
 */
public class AuthcToolsTest {

	public static void main(String[] args) {
		{
			String password = "1234567";
			AuthcKit.AuthcToken authc = AuthcKit.encrypt(new AuthcKit.AuthcTokenImpl(), password);
			System.out.printf("salt:%s, pwd:%s%n", authc.getSalt(), authc.getPassword());
		}
		{
			AuthcKit.AuthcToken authc = new AuthcKit.AuthcTokenImpl();
			authc.setSalt("U3LW4TKkkc");
			authc.setPassword("$2a$10$5nvidOZdVFW1n69oNisop.2pyxNnpQh6W5ezpO5LZs.YIzAkypRxe");
			System.out.printf("check: %s%n", AuthcKit.matches(authc, "1234567"));
		}
	}
}

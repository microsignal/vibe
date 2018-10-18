package io.microvibe.booster.core.lang.spel;

import io.microvibe.booster.core.lang.LocalDataBinding;

import java.math.BigDecimal;

/**
 * @author Qt
 * @since Jul 21, 2018
 */
public class SpringSpelsTest {

	public static void main(String[] args) {
		LocalDataBinding.put("end", "!");
		System.out.println(SpringSpels.eval("('Hello'+'World').concat(#end)"));

		System.out.println(SpringSpels.eval(" 1 + 2 + 3 * 4 + 5 * ( 6 + 7 )").getClass());
		System.out.println(SpringSpels.eval(" 1 + 2 + 3 * 4 + 5 * ( 6 + 7 )"));

		System.out.println(SpringSpels.eval(" 1.7 * (2.8+ 1.1* (2.8+ 1.1))").getClass());
		System.out.println(SpringSpels.eval(" 1.7 * (2.8+ 1.1* (2.8+ 1.1))"));

		System.out.println(SpringSpels.eval(" 1.7 * (2.8+ 1.1* (2.8+ 1.1))", BigDecimal.class).getClass());
		System.out.println(SpringSpels.eval(" 1.7 * (2.8+ 1.1* (2.8+ 1.1))", BigDecimal.class));
		System.out.println(1.7 * (2.8 + 1.1 * (2.8 + 1.1)));


		LocalDataBinding.put("a", new BigDecimal("1.23456"));
		LocalDataBinding.put("b", new BigDecimal("1.23456"));
		System.out.println(SpringSpels.eval("#a .multiply( #b)").getClass());
		System.out.println(SpringSpels.eval("#a .multiply( #b)"));
		System.out.println(SpringSpels.eval("#a + #b").getClass());
		System.out.println(SpringSpels.eval("#a + #b"));
		LocalDataBinding.clear();
	}

}


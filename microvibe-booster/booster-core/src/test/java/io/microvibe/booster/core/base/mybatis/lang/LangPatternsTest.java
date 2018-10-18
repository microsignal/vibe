package io.microvibe.booster.core.base.mybatis.lang;

/**
 * @author Qt
 * @since May 28, 2018
 */
public class LangPatternsTest {

	public static void main(String[] args) {
		System.out.println(LangPatterns.parse(
			"select * from tab where id = {{ #set($a = 1 + 2) $a }} and name = #{name}"));
	}

}

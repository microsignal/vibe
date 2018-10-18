package io.microvibe.util.matcher;

import org.junit.Test;

public class DefaultPathMatchersTest {

	@Test
	public void test() {
		{
			PathMatcher m = DefaultPathMatchers.buildPathMatcher(':');
			System.out.println(m.match("admin:*", "admin:123"));
			System.out.println(m.match("admin:*", "admin:12:3"));
			System.out.println(m.match("admin:??:?", "admin:12:3"));
			System.out.println(m.match("admin:**", "admin:12:3"));
		}
		{
			AntPathMatcher m = new AntPathMatcher(":");
			System.out.println(m.match("admin:*", "admin:123"));
			System.out.println(m.match("admin:*", "admin:12:3"));
			System.out.println(m.match("admin:??:?", "admin:12:3"));
			System.out.println(m.match("admin:**", "admin:12:3"));
		}
	}
}

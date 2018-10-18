package io.microvibe.booster.core.base.shiro.authz;

import org.apache.shiro.util.AntPathMatcher;

/**
 * @author Qt
 * @since May 28, 2018
 */
public class DefaultPathMatchersTest {
	public static void main(String[] args) {
		{
			PathMatcher m = DefaultPathMatchers.buildPathMatcher(':');
			System.out.println(m.match("admin:*", "admin:123"));
			System.out.println(m.match("admin:*", "admin:12:3"));
			System.out.println(m.match("admin:??:?", "admin:12:3"));
			System.out.println(m.match("admin:**", "admin:12:3"));
		}
		{
			org.apache.shiro.util.AntPathMatcher m = new AntPathMatcher();
			m.setPathSeparator(":");
			System.out.println(m.match("admin:*", "admin:123"));
			System.out.println(m.match("admin:*", "admin:12:3"));
			System.out.println(m.match("admin:**", "admin:12:3"));
		}
	}
}

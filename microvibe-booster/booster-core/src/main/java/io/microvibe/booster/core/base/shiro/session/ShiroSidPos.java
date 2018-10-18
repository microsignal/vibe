package io.microvibe.booster.core.base.shiro.session;

public class ShiroSidPos {

	private static final ThreadLocal<String> local = new ThreadLocal<>();

	public static String pop() {
		String sid = local.get();
		if (sid != null)
			local.remove();
		return sid;
	}

	public static void set(String sid) {
		local.set(sid);
	}
}

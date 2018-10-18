package io.microvibe.booster.core.base.shiro.authz;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.util.AntPathMatcher;

import java.util.regex.Pattern;

@Slf4j
public class DefaultPathMatchers {

	public static final char PATH_SEPERATOR = '/';

	static final PathMatcher defaultPathMatcher = new DefaultPathMatcher(PATH_SEPERATOR);

	@RequiredArgsConstructor
	static class DefaultPathMatcher implements PathMatcher {

		final char pathSeperator;

		@Override
		public boolean match(String pattern, String path) {
			pattern = toCanonicalPattern(pattern);
			path = toCanonicalPath(path);
			pattern = "^" + pattern + "$";
			log.debug("pattern: {}", pattern);
			log.debug("path: {}", path);
			boolean matches = Pattern.compile(pattern).matcher(path).find();
			return matches;
		}

		@Override
		public boolean matchStart(String pattern, String path) {
			pattern = toCanonicalPattern(pattern);
			path = toCanonicalPath(path);
			pattern = "^" + pattern;
			log.debug("pattern: {}", pattern);
			log.debug("path: {}", path);
			boolean matches = Pattern.compile(pattern).matcher(path).find();
			return matches;
		}

		String toCanonicalPattern(String pattern) {
			pattern = pattern.replaceAll("(" + Pattern.quote(String.valueOf(pathSeperator)) + ")+$", "")
				+ pathSeperator;// 结尾分隔符标准化
			String pathLetter = "[^\\" + pathSeperator + "]*";
			String pathUnit = "[^\\" + pathSeperator + "]*";
			String pathUnits = "(" + pathSeperator + pathUnit + ")*";
			char[] chs = pattern.toCharArray();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < chs.length; i++) {
				if (chs[i] == '*') {
					if (i == chs.length - 1 || chs[i + 1] != '*') {
						sb.append(pathUnit);
					} else {
						if (i == 0) {
							sb.append(pathUnits);
						} else if (chs[i - 1] == pathSeperator) {
							sb.deleteCharAt(sb.length() - 1);
							sb.append(pathUnits);
						} else {
							sb.append(pathUnit);
						}
						while (chs[i + 1] == '*') {
							i++;
						}
					}
				} else if (chs[i] == '?') {
					sb.append(pathLetter);
				} else {
					sb.append(chs[i]);
				}
			}
			pattern = sb.toString();
			return pattern;
		}

		String toCanonicalPath(String path) {
			return path.replaceAll("(" + Pattern.quote(String.valueOf(pathSeperator)) + ")+$", "") + pathSeperator;// 结尾分隔符标准化
		}
	}

	public static PathMatcher buildPathMatcher(char pathSeperator) {
		return new DefaultPathMatcher(pathSeperator);
	}

	public static PathMatcher getDefaultPathMatcher() {
		return defaultPathMatcher;
	}

	public static boolean match(String pattern, String path) {
		return defaultPathMatcher.match(pattern, path);
	}

	public static boolean matchStart(String pattern, String path) {
		return defaultPathMatcher.matchStart(pattern, path);
	}

}

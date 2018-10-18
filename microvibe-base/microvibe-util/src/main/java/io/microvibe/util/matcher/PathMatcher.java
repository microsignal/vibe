package io.microvibe.util.matcher;

public interface PathMatcher {

	boolean match(String pattern, String path);

	boolean matchStart(String pattern, String path);
}

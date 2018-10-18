package io.microvibe.booster.core.search;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Searches {
	public static final String DELIMITER = "__";

	public static SearchKey toSearchKey(String searchProperty, ISymbol searchSymbol) {
		int i = searchProperty.lastIndexOf(DELIMITER);
		if (i > 0 && i < searchProperty.length()) {
			return new SearchKey(searchProperty.substring(0, i), searchSymbol);
		} else {
			return new SearchKey(searchProperty, searchSymbol);
		}
	}

	public static String toSearchKeyString(String searchProperty, ISymbol searchSymbol) {
		return toSearchKey(searchProperty, searchSymbol).toString();
	}

	public static SearchKey parseSearchKey(String searchKeyString) {
		int i = searchKeyString.lastIndexOf(DELIMITER);
		if (i > 0 && i < searchKeyString.length()) {
			try {
				ISymbol searchSymbol = SearchOper.valueOf(searchKeyString.substring(i + DELIMITER.length()));
				return new SearchKey(searchKeyString.substring(0, i), searchSymbol);
			} catch (IllegalArgumentException e) {
				return new SearchKey(searchKeyString, SearchOper.eq);
			}
		} else {
			return new SearchKey(searchKeyString, SearchOper.eq);
		}
	}

	public static String quoteWildcard(Object arg) {
		if (arg == null || arg instanceof String && ((String) (arg = ((String) arg).trim())).length() == 0) {
			return null;
		}
		String s = arg.toString();
		if (!s.startsWith("%")) {
			s = "%" + s;
		}
		if (!s.endsWith("%")) {
			s = s + "%";
		}
		return s;
	}

	public static String prependWildcard(Object arg) {
		if (arg == null || arg instanceof String && ((String) (arg = ((String) arg).trim())).length() == 0) {
			return null;
		}
		String s = arg.toString();
		if (!s.startsWith("%")) {
			s = "%" + s;
		}
		return s;
	}

	public static String appendWildcard(Object arg) {
		if (arg == null || arg instanceof String && ((String) (arg = ((String) arg).trim())).length() == 0) {
			return null;
		}
		String s = arg.toString();
		if (!s.endsWith("%")) {
			s = s + "%";
		}
		return s;
	}

	public static ISymbol[] symbols() {
		return SearchOper.values();
	}

	public static ISymbol symbolOf(String symbolName) {
		return SearchOper.valueOf(symbolName);
	}


}

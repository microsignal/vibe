package io.microvibe.util.script;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

public class JSUtil {

	static void writeTab(Writer writer, int count) throws IOException {
		char[] c = new char[count];
		Arrays.fill(c, '\t');
		writer.write(c);
	}

	public static String readJSBody(String s) {
		StringBuilder sb = new StringBuilder();
		char[] chs = s.toCharArray();
		int idx = 0;
		try {
			StringBuilder currentLine = new StringBuilder();
			int forLoopLine = 0;
			boolean escape = false;// \
			boolean regexp = false;// //
			boolean regexpBracket = false;// []
			boolean wordEnd = true;
			boolean quot = false; // ' "
			char lastQuot = 0;
			char lastChar = 0;
			int tabCount = 0;
			char[] tabs = null;
			loop: for (int i = 0; i < chs.length; i++) {
				char c = chs[i];
				idx = i;
				beforeWrite: {
					if (escape) {
						escape = false;
						break beforeWrite;
					}
					if (regexp) {
						if (c == ']' && regexpBracket) {
							regexpBracket = false;
						} else if (c == '[' && !regexpBracket) {
							regexpBracket = true;
						} else if (c == '/' && !regexpBracket) {
							regexp = false;
						}
						break beforeWrite;
					}
					if (quot) {
						switch (c) {
						case '\\':
							escape = true;
							sb.append(c);
							lastChar = c;
							continue loop;
						case '"':
						case '\'':
							if (lastQuot == c) {
								quot = false;
								lastQuot = 0;
							}
						default:
							sb.append(c);
							lastChar = c;
							continue loop;
						}
					} else {
						switch (c) {
						case '\\':
							escape = true;
							break beforeWrite;
						case '"':
						case '\'':
							quot = true;
							lastQuot = c;
							break beforeWrite;
						case '/':
							if (lastChar == ')' || lastChar == '$' || lastChar == '_' || (lastChar >= 'a' && lastChar <= 'z')
									|| (lastChar >= 'A' && lastChar <= 'Z') || (lastChar >= '0' && lastChar <= '9')) {
								wordEnd = false;
							} else {
								wordEnd = true;
							}
							if (wordEnd) {
								regexp = true;
								wordEnd = false;
							}
							break beforeWrite;
						case '{':
							tabCount++;
							break beforeWrite;
						case '}':
							tabCount--;
							if (lastChar != '{') {
								sb.append("\r\n");
								tabs = new char[// Math.max(0,
								tabCount
								// )
								];
								Arrays.fill(tabs, '\t');
								sb.append(tabs);
							}
							sb.append(c);
							lastChar = c;
							continue loop;
						default:
							break beforeWrite;
						}
					}
				}
				if (!escape && !quot && !regexp) {
					if (forLoopLine > 0) {
						if (currentLine.length() > 7 && currentLine.indexOf(" in ") > 0) {
							forLoopLine = 3;
						} else if (lastChar == ';') {
							forLoopLine++;
						}
					}
					if (forLoopLine == 0 || forLoopLine > 3 || forLoopLine == 3 && lastChar != ';') {
						if ((lastChar == ';') || (lastChar == '{' && c != '}') || (lastChar == '}' && c != ';' && c != '(' && c != ')')) {
							sb.append("\r\n");
							tabs = new char[ // Math.max(0,
							tabCount
							// )
							];
							Arrays.fill(tabs, '\t');
							sb.append(tabs);
							currentLine = new StringBuilder();
							forLoopLine = 0;
						}
					}
				}
				if (c > 32) {
					lastChar = c;
				}
				currentLine.append(c);
				if (currentLine.toString().startsWith("for(") && forLoopLine == 0) {
					forLoopLine = 1;
				}
				sb.append((c));
			}
			if (regexp || escape) {
				throw new IllegalArgumentException();
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			char[] xchs = Arrays.copyOfRange(chs, Math.max(0, idx - 20), Math.min(chs.length, idx + 20));
			e = new IllegalArgumentException("idx " + idx + " : " + chs[idx] + "; source: " + new String(xchs) + "", e);
			e.printStackTrace();
		}
		return sb.toString();
	}
}

package io.microvibe.castor.support;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.WeakHashMap;
import java.util.function.Function;

public class DateCastor extends AbstractMarshallableCastor<Date> {

	/*
	public static void main(String[] args) throws ParseException {
		System.out.println(DateFormat.getDateInstance().format(new Date()));
		System.out.println(DateFormat.getDateTimeInstance().format(new Date()));
		System.out.println(DateFormat.getTimeInstance().format(new Date()));
		System.out.println(DateFormat.getInstance().format(new Date()));
		System.out.println("20171214".matches("\\d{8}"));

		DateCastor c = new DateCastor(Date.class);
		Date d = c.fromString("23:29:27");
		System.out.println(d);
	}
	*/

	class DatePattern {
		String datePattern;
		String datePatternRegex;

		public DatePattern(String datePattern, String datePatternRegex) {
			this.datePattern = datePattern;
			this.datePatternRegex = datePatternRegex;
		}
	}

	private List<Function<String, Date>> funcs = new ArrayList<Function<String, Date>>();
	private DatePattern[] datePatterns = {
			new DatePattern("yyyyMMdd", "\\d{8}"),
			new DatePattern("yyyyMMddHHmmss", "\\d{14}"),
			new DatePattern("yyyy-MM-dd", "\\d{1,4}-\\d{1,2}-\\d{1,2}"),
			new DatePattern("yyyy-MM-dd HH:mm:ss",
					"\\d{1,4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}"),
			new DatePattern("HH:mm:ss",
					"\\d{1,2}:\\d{1,2}:\\d{1,2}"),
			new DatePattern("yyyyMMddHHmmssSSS", "\\d{17}"),
			new DatePattern("yyyy-MM-dd HH:mm:ss.SSS",
					"\\d{1,4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{1,3}"),
	};

	private ThreadLocal<DateFormat[]> dateFormatsLocal = new ThreadLocal<>();
	private ThreadLocal<WeakHashMap<String, DateFormat>> dateFormatCacheLocal = new ThreadLocal<>();

	public DateCastor(Class<Date> type) {
		super(type);
		funcs.add((str) -> {
			DateFormat[] dateFormats = getDefDateFormats();
			for (DateFormat dateFormat : dateFormats) {
				try {
					return dateFormat.parse(str);
				} catch (ParseException e) {
				}
			}
			return null;
		});
		funcs.add((str) -> {
			DatePattern[] datePatterns = this.datePatterns;
			for (DatePattern datePattern : datePatterns) {
				String pattern = datePattern.datePattern;
				String datePatternRegex = datePattern.datePatternRegex;
				if (str.matches(datePatternRegex)) {
					try {
						return getDefDateFormat(pattern).parse(str);
					} catch (Exception e) {
					}
				}
			}
			return null;
		});
		funcs.add((str) -> {
			try {
				return new Date(Long.parseLong(str));
			} catch (final NumberFormatException e) {
				return null;
			}
		});
	}

	private DateFormat[] getDefDateFormats() {
		DateFormat[] dateFormats = dateFormatsLocal.get();
		if (dateFormats == null) {
			dateFormats = new DateFormat[] {
					DateFormat.getDateTimeInstance(),
					DateFormat.getDateInstance(),
					DateFormat.getTimeInstance(),
					DateFormat.getInstance(),
			};
			dateFormatsLocal.set(dateFormats);
		}
		return dateFormats;
	}

	private DateFormat getDefDateFormat(String pattern) {
		WeakHashMap<String, DateFormat> weakHashMap = dateFormatCacheLocal.get();
		if (weakHashMap == null) {
			weakHashMap = new WeakHashMap<>();
			dateFormatCacheLocal.set(weakHashMap);
		}
		DateFormat dateFormat = weakHashMap.get(pattern);
		if (dateFormat == null) {
			dateFormat = new SimpleDateFormat(pattern);
			weakHashMap.put(pattern, dateFormat);
		}
		return dateFormat;
	}

	@Override
	public Date castFromBasic(Object orig) {
		return fromString(orig.toString());
	}

	@Override
	public String toString(Date o) {
		return DateFormat.getDateInstance().format(o);
	}

	@Override
	public Date fromString(String str) {
		str = str.trim();
		Date date = null;
		for (Function<String, Date> func : funcs) {
			date = func.apply(str);
			if (date != null) {
				break;
			}
		}
		return date;
	}
}

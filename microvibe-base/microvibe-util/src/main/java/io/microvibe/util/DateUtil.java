package io.microvibe.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static Date currentDate() {
		return new Date();
	}

	public static Timestamp currentTimestamp() {
		return new Timestamp(System.currentTimeMillis());
	}

	public static String getDate8String() {
		return getDateString("yyyyMMdd");
	}

	public static String getDate8String(Date date) {
		return getDateString("yyyyMMdd", date);
	}

	public static String getDate10String() {
		return getDateString("yyyy-MM-dd");
	}

	public static String getDate10String(Date date) {
		return getDateString("yyyy-MM-dd", date);
	}

	public static String getDate14String() {
		return getDateString("yyyyMMddHHmmss");
	}

	public static String getDate14String(Date date) {
		return getDateString("yyyyMMddHHmmss", date);
	}

	public static String getDate17String() {
		return getDateString("yyyyMMddHHmmssSSS");
	}

	public static String getDate17String(Date date) {
		return getDateString("yyyyMMddHHmmssSSS", date);
	}

	public static String getDate19String() {
		return getDateString("yyyy-MM-dd HH:mm:ss");
	}

	public static String getDate19String(Date date) {
		return getDateString("yyyy-MM-dd HH:mm:ss", date);
	}

	public static String getDate23String() {
		return getDateString("yyyy-MM-dd HH:mm:ss.SSS");
	}

	public static String getDate23String(Date date) {
		return getDateString("yyyy-MM-dd HH:mm:ss.SSS", date);
	}

	public static String getDateString(String pattern) {
		return new SimpleDateFormat(pattern).format(new Date());
	}

	public static String getDateString(String pattern, Date date) {
		return new SimpleDateFormat(pattern).format(date);
	}

	public static Date parseDate8(String source) throws ParseException {
		return parseDate(source, "yyyyMMdd");
	}

	public static Date parseDate10(String source) throws ParseException {
		return parseDate(source, "yyyy-MM-dd");
	}

	public static Date parseDate14(String source) throws ParseException {
		return parseDate(source, "yyyyMMddHHmmss");
	}

	public static Date parseDate17(String source) throws ParseException {
		return parseDate(source, "yyyyMMddHHmmssSSS");
	}

	public static Date parseDate19(String source) throws ParseException {
		return parseDate(source, "yyyy-MM-dd HH:mm:ss");
	}

	public static Date parseDate23(String source) throws ParseException {
		return parseDate(source, "yyyy-MM-dd HH:mm:ss.SSS");
	}

	public static Date parseDate(String source, String pattern) throws ParseException {
		return new SimpleDateFormat(pattern).parse(source);
	}

	private DateUtil() {
	}
}

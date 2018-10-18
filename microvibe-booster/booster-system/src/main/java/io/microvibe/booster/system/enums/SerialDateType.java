package io.microvibe.booster.system.enums;

import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.core.base.entity.EntryableAdaptor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 序列号时间类型
 */
@Getter
@RequiredArgsConstructor
public enum SerialDateType implements EntryableAdaptor {

	NONE("", "", 0),
	YY("YY", "yy", SerialDateType.YEAR_TYPE),
	YYMM("YYMM", "yyMM", SerialDateType.YEAR_TYPE | SerialDateType.MONTH_TYPE),
	YYMMDD("YYMMDD", "yyMMdd", SerialDateType.YEAR_TYPE | SerialDateType.MONTH_TYPE | SerialDateType.DAY_TYPE),
	YYYY("YYYY", "yyyy", SerialDateType.YEAR_TYPE),
	YYYYMM("YYYYMM", "yyyyMM", SerialDateType.YEAR_TYPE | SerialDateType.MONTH_TYPE),
	YYYYMMDD("YYYYMMDD", "yyyyMMdd", SerialDateType.YEAR_TYPE | SerialDateType.MONTH_TYPE | SerialDateType.DAY_TYPE),
	MMDD("MMDD", "MMdd", SerialDateType.YEAR_TYPE | SerialDateType.MONTH_TYPE | SerialDateType.DAY_TYPE),
	//
	;

	public static final int YEAR_TYPE = 1;
	public static final int MONTH_TYPE = 1 << 1;
	public static final int DAY_TYPE = 1 << 2;

	private final String info;
	private final String pattern;
	private final int type;


	public boolean hasYear() {
		return (YEAR_TYPE & type) > 0;
	}

	public boolean hasMonth() {
		return (MONTH_TYPE & type) > 0;
	}

	public boolean hasDay() {
		return (DAY_TYPE & type) > 0;
	}


	public String now() {
		if (StringUtils.isBlank(getPattern())) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(getPattern());
		String date = sdf.format(new Date());
		return date;
	}
}

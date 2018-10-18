package io.microvibe.booster.system.enums;

import io.microvibe.booster.core.base.entity.EntryableAdaptor;
import lombok.Getter;

/**
 * <P>
 * Company： http://www.transnal.com
 * </P>
 * <P>
 * Description： io.microvibe.booster.system.enums
 * </P>
 *
 * @author wuzhe1159@163.com2018-07-24 19:03
 */
@Getter
public enum SourceTypeEnum implements EntryableAdaptor {

	/**
	 * NC远端同步
	 */
	NC("NC"),

	/**
	 *本地添加
	 */
	LOCAL("大宗物料"),


	/**
	 * 其他
	 */
	OTHER("其他");
	private final String info;

	private SourceTypeEnum(String info) {
		this.info = info;
	}
}

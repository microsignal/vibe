package io.microvibe.booster.core.base.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(Character[].class)
@MappedJdbcTypes({JdbcType.VARCHAR,JdbcType.CHAR})
public class CharacterArrayTypeHandler extends StringTokenizerTypeHandler<Character> {
	public CharacterArrayTypeHandler() {
		super(Character.class);
	}

	@Override
	Character parseString(String value) {
		return value.charAt(0);
	}
}

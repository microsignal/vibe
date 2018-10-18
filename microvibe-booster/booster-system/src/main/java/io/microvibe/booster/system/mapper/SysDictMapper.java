package io.microvibe.booster.system.mapper;

import io.microvibe.booster.core.base.mybatis.mapping.BaseMapper;
import io.microvibe.booster.core.base.mybatis.annotation.AfterEntityScanner;
import io.microvibe.booster.core.base.mybatis.lang.VelocityLangDriver;
import io.microvibe.booster.system.entity.SysDict;
import org.apache.ibatis.annotations.*;

/**
 * @author Qt
 * @since Jun 19, 2018
 */
@Mapper
@AfterEntityScanner
public interface SysDictMapper extends BaseMapper<SysDict, Long> {

	@Lang(VelocityLangDriver.class)
	@Select({"{{",
		"select $sql.columnsAlias('SysDict', 'd') ",
		"from $sql.table('SysDict', 'd') ",
		"where $sql.column('SysDict','dictType','d') = #{dictType} ",
		"and $sql.column('SysDict','dictName','d') = #{dictName} ",
		"and $sql.column('SysDict','deleted','d') = 0",
		"}} "})
	@ResultType(SysDict.class)
	SysDict getDictByCode(@Param("dictType") String dictType, @Param("dictName") String dictName);

	@Lang(VelocityLangDriver.class)
	@Select({"{{",
		"select $sql.column('SysDict','dictValue','d') ",
		"from $sql.table('SysDict','d') ",
		"where $sql.column('SysDict','dictType','d') = #{dictType} ",
		"and $sql.column('SysDict','dictName','d') = #{dictName} ",
		"and $sql.column('SysDict','deleted','d') = 0",
		"}} "})
	@ResultType(String.class)
	String getDictValueByCode(@Param("dictType") String dictType, @Param("dictName") String dictName);


////	@Lang(VelocityLangDriver.class)
////	@Select({"{{",
////		"select ${@io.microvibe.booster.core.base.mybatis.builder.Expr@columns()} ",
////		"from $sql.table('SysDict') ",
////		"<where>",
////		"${@io.microvibe.booster.core.base.mybatis.builder.Expr@where(example)}"
////		,"</where>",
////		"}}"})
//	@AutoStatement(BuilderType.SELECT_BY_EXAMPLE)
//	List<SysDict> selectByExample(Example<SysDict> example);
}

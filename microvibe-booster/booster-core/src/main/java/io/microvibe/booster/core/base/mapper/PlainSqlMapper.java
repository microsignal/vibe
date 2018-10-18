package io.microvibe.booster.core.base.mapper;

import io.microvibe.booster.core.base.mybatis.annotation.AfterEntityScanner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @since Jul 31, 2018
 */
@Mapper
@AfterEntityScanner
public interface PlainSqlMapper {

	long count(Map<String, Object> param);

	Map<String, Object> get(Map<String, Object> param);

	List<Map<String, Object>> select(Map<String, Object> param);

	List<Map<String, Object>> select(Map<String, Object> param, RowBounds rowBounds);

	int update(Map<String, Object> param);

	int delete(Map<String, Object> param);

	int insert(Map<String, Object> param);
}

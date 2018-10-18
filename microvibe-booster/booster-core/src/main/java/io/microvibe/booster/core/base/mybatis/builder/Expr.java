package io.microvibe.booster.core.base.mybatis.builder;

import com.alibaba.fastjson.JSONObject;
import io.microvibe.booster.commons.string.StringBindings;
import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.commons.utils.property.PropertyUtil;
import io.microvibe.booster.core.base.mybatis.MybatisConstants;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

/**
 * @author Qt
 * @since Jun 21, 2018
 */
public class Expr {

	public static final String NAME = Expr.class.getName();

	public static boolean hasPathProperty(Object _parameter, String propertyName) {
		try {
			Object val = PropertyUtil.getPathProperty(_parameter, propertyName);
			return isNotEmpty(val);
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean hasProperty(Object _parameter, String propertyName) {
		try {
			Object val = PropertyUtil.getProperty(_parameter, propertyName);
			return isNotEmpty(val);
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isNotEmpty(Object val) {
		if (val == null) {
			return false;
		}
		if (val instanceof String) {
			if (val.toString().trim().length() == 0) {
				return false;
			}
		}
		return true;
	}

	public static boolean hasWhereExtClause(Object _parameter) {
		return hasProperty(_parameter, MybatisConstants.PARAM_WHERE_EXTENSION_CLAUSE);
	}

	public static boolean hasOrderByClause(Object _parameter) {
		return hasProperty(_parameter, MybatisConstants.PARAM_ORDER_BY);
	}

	public static String withAlias(String sql, String alias) {
		alias = StringUtils.trimToEmpty(alias);
		String aliasPrefix = "";
		if (StringUtils.isNotBlank(alias)) {
			aliasPrefix = alias + ".";
		}
		return sql.replaceAll("\\$\\{alias\\}\\.|\\$alias\\.", aliasPrefix)
			.replaceAll("\\$\\{alias\\}\\b|\\$alias\\b", alias);
	}
}

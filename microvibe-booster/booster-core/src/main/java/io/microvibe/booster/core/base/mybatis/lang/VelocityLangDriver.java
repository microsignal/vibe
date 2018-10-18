package io.microvibe.booster.core.base.mybatis.lang;

import io.microvibe.booster.core.lang.velocity.VelocityContextLocal;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.Alias;

/**
 * <code>
 * bind-vars:<br>
 * sql -> {@linkplain SQLToolkit}
 * <ul>
 * <li></li>
 * </ul>
 * </code>
 * @author Qt
 * @since Jul 24, 2018
 */
@Slf4j
@Alias("velocity")
public class VelocityLangDriver extends AbstractLangDriver {

	@Override
	public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
		try {
			VelocityContextLocal.put("sql", SQLToolkit.instance());

			script = LangPatterns.parse(script);
			script = ScriptWrapper.quoteScript(script);

			return super.createSqlSource(configuration, script, parameterType);
		} catch (RuntimeException e) {
			log.error("parse error : {}", script);
			throw e;
		} finally {
			VelocityContextLocal.clear();
		}
	}
}

package io.microvibe.booster.core.base.mybatis.configuration;

import io.microvibe.booster.commons.utils.IOUtils;
import io.microvibe.booster.core.api.tools.XmlToolKit;
import io.microvibe.booster.core.base.mybatis.MybatisConstants;
import io.microvibe.booster.core.base.persistence.EntityMetaData;
import io.microvibe.booster.core.base.persistence.FieldMetaData;
import io.microvibe.booster.core.base.persistence.JoinMetaData;
import io.microvibe.booster.core.base.persistence.PersistentRecognizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Qt
 * @since Aug 25, 2018
 */
@Slf4j
public class PersistentEnhancerBuilder {

	/**
	 * 生成ResultMap
	 *
	 * @param entity 实体
	 * @return
	 */
	public static void buildResultMap(Configuration configuration, Class<?> entity) {
		try {
			String resource = "buildDefaultResultMap:" + entity.getName();
			InputStream inputStream = createResultMapInputStream(entity);
			XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(inputStream,
				configuration, resource, configuration.getSqlFragments());
			xmlMapperBuilder.parse();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 生成ResultMap, 创建多种映射(命名空间为实体类路径) :
	 * <ul>
	 * <li>${entity.getName()} .ResultMap:  含表连接信息的映射, 不转换列名的属性别名</li>
	 * <li>${entity.getName()} .AliasResultMap:  含表连接信息的映射, 转换列名的属性别名</li>
	 * <li>${entity.getName()} .ResultMapNoJoin:  不含表连接信息的映射, 不转换列名的属性别名</li>
	 * <li>${entity.getName()} .AliasResultMapNoJoin:  不含表连接信息的映射, 转换列名的属性别名</li>
	 * </ul>
	 *
	 * @param entity 实体
	 * @return
	 */
	public static InputStream createResultMapInputStream(Class<?> entity) throws DocumentException, IOException, SAXException {
		InputStream in = PersistentEnhancerBuilder.class.getResourceAsStream("mapper-template.xml");
		try {
			SAXReader reader = new SAXReader();
			reader.setEntityResolver(new NoOpEntityResolver());
			reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			Document doc = reader.read(in);
			Element root = doc.getRootElement();
			root.clearContent();
			root.addAttribute("namespace", entity.getName());
			root.add(createResultMapElement(MybatisConstants.AUTO_ENTITY_RESULT_MAP,
				entity, true, false));
			root.add(createResultMapElement(MybatisConstants.AUTO_ENTITY_ALIAS_RESULT_MAP,
				entity, true, true));
			root.add(createResultMapElement(MybatisConstants.AUTO_ENTITY_RESULT_MAP_NO_JOIN,
				entity, false, false));
			root.add(createResultMapElement(MybatisConstants.AUTO_ENTITY_ALIAS_RESULT_MAP_NO_JOIN,
				entity, false, true));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			XMLWriter xmlWriter = new XMLWriter(new PrintWriter(bos), XmlToolKit.createPrettyPrint());
			xmlWriter.write(doc);
			xmlWriter.flush();
			xmlWriter.close();
			if (log.isDebugEnabled()) {
				log.debug(new String(bos.toByteArray()));
			}
			return new ByteArrayInputStream(bos.toByteArray());
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * 生成ResultMap
	 *
	 * @param id     ResultMap-id
	 * @param entity 实体
	 * @return
	 */
	public static void buildResultMap(Configuration configuration, String id, Class<?> entity) {
		buildResultMap(configuration, id, entity, true, false, (Collection<JoinMetaData>) null);
	}

	/**
	 * 生成ResultMap
	 *
	 * @param id             ResultMap-id
	 * @param entity         实体
	 * @param withJoinTables 是否包含实体默认的表连接配置
	 * @return
	 */
	public static void buildResultMap(Configuration configuration, String id, Class<?> entity,
		boolean withJoinTables) {
		buildResultMap(configuration, id, entity, withJoinTables, false, (Collection<JoinMetaData>) null);
	}

	/**
	 * 生成ResultMap
	 *
	 * @param id                ResultMap-id
	 * @param entity            实体
	 * @param withJoinTables    是否包含实体默认的表连接配置
	 * @param propertyAliasable 是否将列名转换为实体属性别名
	 * @return
	 */
	public static void buildResultMap(Configuration configuration, String id, Class<?> entity,
		boolean withJoinTables, boolean propertyAliasable) {
		buildResultMap(configuration, id, entity, withJoinTables, propertyAliasable, (Collection<JoinMetaData>) null);
	}

	/**
	 * 生成ResultMap
	 *
	 * @param id                ResultMap-id
	 * @param entity            实体
	 * @param withJoinTables    是否包含实体默认的表连接配置
	 * @param propertyAliasable 是否将列名转换为实体属性别名
	 * @param joinMetaDatas     附加的表连接配置
	 * @return
	 */
	public static void buildResultMap(Configuration configuration, String id, Class<?> entity,
		boolean withJoinTables, boolean propertyAliasable, Collection<JoinMetaData> joinMetaDatas) {
		try {
			String resource = "buildResultMap:" + id;
			InputStream inputStream = createResultMapInputStream(id, entity, withJoinTables, propertyAliasable, joinMetaDatas);
			XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(inputStream,
				configuration, resource, configuration.getSqlFragments());
			xmlMapperBuilder.parse();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 生成ResultMap
	 *
	 * @param id                ResultMap-id
	 * @param entity            实体
	 * @param withJoinTables    是否包含实体默认的表连接配置
	 * @param propertyAliasable 是否将列名转换为实体属性别名
	 * @param joinMetaDatas     附加的表连接配置
	 * @return
	 */
	public static InputStream createResultMapInputStream(String id, Class<?> entity,
		boolean withJoinTables, boolean propertyAliasable, Collection<JoinMetaData> joinMetaDatas)
		throws DocumentException, IOException, SAXException {
		InputStream in = PersistentEnhancerBuilder.class.getResourceAsStream("mapper-template.xml");
		try {
			SAXReader reader = new SAXReader();
			reader.setEntityResolver(new NoOpEntityResolver());
			reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			Document doc = reader.read(in);
			Element root = doc.getRootElement();
			root.clearContent();

			int i = id.lastIndexOf('.');
			String namespace = "";
			if (i > 0) {
				namespace = id.substring(0, i);
				id = id.substring(i + 1);
			}


			root.addAttribute("namespace", namespace);
			root.add(createResultMapElement(id, entity, withJoinTables, propertyAliasable, joinMetaDatas));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			XMLWriter xmlWriter = new XMLWriter(new PrintWriter(bos), XmlToolKit.createPrettyPrint());
			xmlWriter.write(doc);
			xmlWriter.flush();
			xmlWriter.close();
			if (log.isDebugEnabled()) {
				log.debug(new String(bos.toByteArray()));
			}
			return new ByteArrayInputStream(bos.toByteArray());
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * 生成ResultMap
	 *
	 * @param id                ResultMap-id
	 * @param entity            实体
	 * @param withJoinTables    是否包含实体默认的表连接配置
	 * @param propertyAliasable 是否将列名转换为实体属性别名
	 * @return
	 */
	public static Element createResultMapElement(String id, Class<?> entity,
		boolean withJoinTables, boolean propertyAliasable) {
		return createResultMapElement(id, entity, withJoinTables, propertyAliasable, (Collection<JoinMetaData>) null);
	}

	/**
	 * 生成ResultMap
	 *
	 * @param id                ResultMap-id
	 * @param entity            实体
	 * @param withJoinTables    是否包含实体默认的表连接配置
	 * @param propertyAliasable 是否将列名转换为实体属性别名
	 * @param joinMetaDatas     附加的表连接配置
	 * @return
	 */
	public static Element createResultMapElement(String id, Class<?> entity,
		boolean withJoinTables, boolean propertyAliasable, Collection<JoinMetaData> joinMetaDatas) {

		Element columnMappings = DocumentHelper.createElement("resultMap");
		columnMappings.addAttribute("id", id);
		columnMappings.addAttribute("type", entity.getName());
		columnMappings.addAttribute("autoMapping", "true");

		EntityMetaData entityMetaData = PersistentRecognizer.entityMetaData(entity);

		// region fields
		{
			List<FieldMetaData> fields = new ArrayList<>();
			fields.addAll(entityMetaData.getAllColumnFields());
			fields.addAll(entityMetaData.getFormulaFields());
			for (FieldMetaData columnMeta : fields) {
				Element columnMapping = createColumnMappingElement(columnMeta, propertyAliasable);
				columnMappings.add(columnMapping);
			}
		}
		// endregion

		// region join-table-fields
		if (withJoinTables) {
			List<JoinMetaData> joinFields = entityMetaData.getJoinFields();
			for (JoinMetaData joinField : joinFields) {
				Element join = createJoinMappingElement(joinField, propertyAliasable);
				columnMappings.add(join);
			}
		}
		// endregion

		// region joinMetaDatas
		if (joinMetaDatas != null) {
			for (JoinMetaData joinField : joinMetaDatas) {
				Element join = createJoinMappingElement(joinField, propertyAliasable);
				columnMappings.add(join);
			}
		}
		// endregion

		return columnMappings;
	}

	private static Element createJoinMappingElement(JoinMetaData joinField, boolean propertyAliasable) {
		Class<?> table = joinField.getTable();
		Element join;
		if (joinField.isOneToMany()) {
			join = DocumentHelper.createElement("collection");
			join.addAttribute("ofType", table.getName());
		} else {
			join = DocumentHelper.createElement("association");
			join.addAttribute("javaType", table.getName());
		}
		join.addAttribute("autoMapping", "true");
		join.addAttribute("property", joinField.getProperty());
		join.addAttribute("columnPrefix", joinField.getColumnPrefix());
		EntityMetaData tableMetaData = joinField.getTableMetaData();
		List<FieldMetaData> tableFields = new ArrayList<>();
		tableFields.addAll(tableMetaData.getAllColumnFields());
		tableFields.addAll(tableMetaData.getFormulaFields());
		for (FieldMetaData columnMeta : tableFields) {
			Element columnMapping = createColumnMappingElement(columnMeta, propertyAliasable);
			join.add(columnMapping);
		}
		return join;
	}

	private static Element createColumnMappingElement(FieldMetaData columnMeta, boolean propertyAliasable) {
		// java 字段名
		String property = columnMeta.getJavaProperty();
		// sql 列名
		String column;
		if (propertyAliasable) {
			column = property;
		} else {
			column = columnMeta.getColumnName();
		}

		Class<?> javaType = columnMeta.getJavaType();
		JdbcType jdbcType = columnMeta.getJdbcType();
		Class<? extends TypeHandler<?>> typeHandlerClass = columnMeta.getTypeHandlerClass();

		Element columnMapping;
		if (columnMeta.isPrimaryKey()) {
			columnMapping = DocumentHelper.createElement("id");
		} else {
			columnMapping = DocumentHelper.createElement("result");
		}

		columnMapping.addAttribute("column", column);
		columnMapping.addAttribute("property", property);
		if (javaType != null) {
			columnMapping.addAttribute("javaType", javaType.getName());
		}
		if (jdbcType != null) {
			columnMapping.addAttribute("jdbcType", jdbcType.name());
		}
		if (typeHandlerClass != null) {
			columnMapping.addAttribute("typeHandler", typeHandlerClass.getName());
		}
		return columnMapping;
	}

	public static class NoOpEntityResolver implements EntityResolver {
		@Override
		public InputSource resolveEntity(String publicId, String systemId) {
			return new InputSource(new ByteArrayInputStream(new byte[0]));
		}
	}
}

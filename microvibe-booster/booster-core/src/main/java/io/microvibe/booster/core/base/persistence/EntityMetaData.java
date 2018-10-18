package io.microvibe.booster.core.base.persistence;

import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.core.base.mybatis.annotation.JoinOn;
import io.microvibe.booster.core.base.mybatis.annotation.TableAlias;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.annotation.AnnotationUtils;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class EntityMetaData {
	static final ThreadLocal<Map<Class, EntityMetaData>> entityCache
		= ThreadLocal.withInitial(() -> new HashMap<>());

	private Class<?> entityClass;
	private String tableName;
	private String entityName;
	private String tableAlias;

	private List<FieldMetaData> pkColumnFields = new ArrayList<>();
	private List<FieldMetaData> normalColumnFields = new ArrayList<>();
	private FieldMetaData versionColumnField;
	private List<FieldMetaData> allColumnFields = new ArrayList<>();
	private List<FieldMetaData> formulaFields = new ArrayList<>();
	private List<JoinMetaData> joinFields = new ArrayList<>();
	private Map<String, FieldMetaData> transientFields = new ConcurrentHashMap<>();

	private Map<String, FieldMetaData> columnMappings;
	private Map<String, FieldMetaData> propertyMappings;
	private Map<String, JoinMetaData> joinMappings;

	public EntityMetaData(Class<?> entityClass) {
		this.entityClass = entityClass;
		try {
			entityCache.get().put(entityClass, this);
			init();
		} finally {
			entityCache.remove();
		}
	}

	/*public static boolean maybeColumn(Field field) {
		return !Modifier.isStatic(field.getModifiers()) &&
			!Modifier.isFinal(field.getModifiers()) &&
			!Modifier.isTransient(field.getModifiers()) &&
			!field.isAnnotationPresent(Transient.class);
	}*/

	private void init() {
		this.tableName = PersistentRecognizer.getTableName(entityClass);
		if (entityClass.isAnnotationPresent(TableAlias.class)) {
			tableAlias = entityClass.getAnnotation(TableAlias.class).value();
		} else {
			tableAlias = TableAlias.DEFAULT;
		}
		this.entityName = PersistentRecognizer.getEntityName(entityClass);

		// handle AttributeOverride
		Map<String, Column> overrideColumns = new HashMap<>();
		Set<String> deletedProperties = new HashSet<>();
		Set<String> deletedColumns = new HashSet<>();
		fetchAttributeOverrides(overrideColumns, deletedProperties, deletedColumns);

		/*List<Field> fields = FieldReflectUtil.getFields(entityClass, field -> {
			return maybeColumn(field) || (field.isAnnotationPresent(Column.class)
				|| field.isAnnotationPresent(Id.class)
				|| field.isAnnotationPresent(PrimaryKey.class)
			);
		});*/
		List<Field> fields = FieldReflectUtil.getFields(entityClass,
			field -> (field.isAnnotationPresent(JoinOn.class)||!Modifier.isTransient(field.getModifiers()))
				&& !Modifier.isStatic(field.getModifiers()));
		for (Field field : fields) {
			if (field.isAnnotationPresent(JoinOn.class)) {
				// join table
				JoinMetaData joinMetaData = new JoinMetaData(this, field,
					field.getAnnotation(JoinOn.class));
				joinFields.add(joinMetaData);
			} else if (Modifier.isTransient(field.getModifiers()) || field.isAnnotationPresent(Transient.class)) {
				// transient fields
				if (field.isAnnotationPresent(Column.class)) {
					Column column = field.getAnnotation(Column.class);
					transientFields.put(field.getName(), new FieldMetaData(this, field));
				}
				continue;
			} else {
				FieldMetaData fieldMetaData = new FieldMetaData(this, field);
				if (overrideColumns.containsKey(fieldMetaData.getJavaProperty())) {
					// repair
					Column column = overrideColumns.get(fieldMetaData.getJavaProperty());
					fieldMetaData.setColumnName(column.name());
					fieldMetaData.setInsertable(column.insertable());
					fieldMetaData.setUpdatable(column.updatable());
				}
				if (deletedProperties.contains(fieldMetaData.getJavaProperty())
					|| deletedColumns.contains(fieldMetaData.getColumnName())) {
					continue;
				}

				if (fieldMetaData.isPrimaryKey()) {
					pkColumnFields.add(fieldMetaData);
				} else if (fieldMetaData.isVersionable()
					&& (long.class.equals(fieldMetaData.getJavaType())
					|| int.class.equals(fieldMetaData.getJavaType())
					|| Number.class.isAssignableFrom(fieldMetaData.getJavaType()))
					) {
					versionColumnField = fieldMetaData;
				} else if (fieldMetaData.isFormulable()) {
					//formula field
					if (StringUtils.isNotBlank(fieldMetaData.getFormula())) {
						formulaFields.add(fieldMetaData);
					}
				} else {
					normalColumnFields.add(fieldMetaData);
				}
			}
		}
		// allColumnFields contains all except formula
		allColumnFields.addAll(pkColumnFields);
		allColumnFields.addAll(normalColumnFields);
		if (versionColumnField != null) {
			allColumnFields.add(versionColumnField);
		}

		// region cache mappings
		Map<String, FieldMetaData> columnMappings = new LinkedHashMap<>();
		Map<String, FieldMetaData> propertyMappings = new LinkedHashMap<>();
		for (FieldMetaData field : allColumnFields) {
			propertyMappings.put(field.getJavaProperty(), field);
			columnMappings.put(field.getColumnName(), field);
		}
		this.columnMappings = Collections.unmodifiableMap(columnMappings);
		this.propertyMappings = Collections.unmodifiableMap(propertyMappings);

		Map<String, JoinMetaData> joinMappings = new LinkedHashMap<>();
		for (JoinMetaData joinField : joinFields) {
			joinMappings.put(joinField.getProperty(), joinField);
		}
		this.joinMappings = Collections.unmodifiableMap(joinMappings);
		// endregion
	}

	private void fetchAttributeOverrides(Map<String, Column> overrideColumns, Set<String> deletedProperties, Set<String> deletedColumns) {
		Set<AttributeOverride> attributeOverrideSet = new HashSet<>();
		Set<AttributeOverrides> attributeOverridesSet = new HashSet<>();
		Class<?> target = this.entityClass;
		while (target != null && !target.equals(Object.class)) {
			AttributeOverride attributeOverride = AnnotationUtils.findAnnotation(target, AttributeOverride.class);
			if (attributeOverride != null) {
				attributeOverrideSet.add(attributeOverride);
			}
			AttributeOverrides attributeOverrides = AnnotationUtils.findAnnotation(target, AttributeOverrides.class);
			if (attributeOverrides != null) {
				attributeOverridesSet.add(attributeOverrides);
			}
			target = target.getSuperclass();
		}
		for (AttributeOverride attributeOverride : attributeOverrideSet) {
			overrideColumns.put(attributeOverride.name(), attributeOverride.column());
			if (StringUtils.isBlank(attributeOverride.name())) {
				deletedColumns.add(attributeOverride.column().name());
			}
			if (StringUtils.isBlank(attributeOverride.column().name())) {
				deletedProperties.add(attributeOverride.name());
			}
		}
		for (AttributeOverrides attributeOverrides : attributeOverridesSet) {
			for (AttributeOverride override : attributeOverrides.value()) {
				overrideColumns.put(override.name(), override.column());
				if (StringUtils.isBlank(override.name())) {
					deletedColumns.add(override.column().name());
				}
				if (StringUtils.isBlank(override.column().name())) {
					deletedProperties.add(override.name());
				}
			}
		}
		/*AttributeOverride attributeOverride = AnnotationUtils.findAnnotation(this.entityClass, AttributeOverride.class);
		if (attributeOverride != null) {
			overrideColumns.put(attributeOverride.name(), attributeOverride.column());
			if (StringUtils.isBlank(attributeOverride.name())) {
				deletedColumns.add(attributeOverride.column().name());
			}
			if (StringUtils.isBlank(attributeOverride.column().name())) {
				deletedProperties.add(attributeOverride.name());
			}
		}
		AttributeOverrides attributeOverrides = AnnotationUtils.findAnnotation(this.entityClass, AttributeOverrides.class);
		if (attributeOverrides != null) {
			for (AttributeOverride override : attributeOverrides.value()) {
				overrideColumns.put(override.name(), override.column());
				if (StringUtils.isBlank(override.name())) {
					deletedColumns.add(override.column().name());
				}
				if (StringUtils.isBlank(override.column().name())) {
					deletedProperties.add(override.name());
				}
			}
		}*/
	}

	public FieldMetaData getFieldMetaData(String javaProperty) {
		return propertyMappings.get(javaProperty);
	}

	public JoinMetaData getJoinMetaData(String javaProperty) {
		return joinMappings.get(javaProperty);
	}

	public FieldMetaData getFieldMetaByColumn(String col) {
		return columnMappings.get(col);
	}


}

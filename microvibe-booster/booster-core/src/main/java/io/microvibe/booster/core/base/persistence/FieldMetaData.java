package io.microvibe.booster.core.base.persistence;

import com.alibaba.fastjson.annotation.JSONField;
import io.microvibe.booster.core.base.mybatis.annotation.PrimaryKey;
import io.microvibe.booster.core.base.mybatis.type.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.BooleanTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

@Getter
@Setter
public class FieldMetaData {
	@JsonIgnore
	@JSONField(serialize = false, deserialize = false)
	private EntityMetaData entity;
	private Field field;
	private boolean primaryKey = false;
	private boolean insertable = true;
	private boolean updatable = true;
	private boolean enumerated;
	private EnumType enumeratedType;
	private boolean versionable;
	private boolean formulable = false;
	private String formula;

	/**
	 * table columnName
	 */
	private String columnName;
	/**
	 * Java fieldName
	 */
	private String javaProperty;
	/**
	 * fieldType
	 */
	private Class<?> javaType;
	/**
	 * mybatis jdbcTypeAlias
	 */
	private String jdbcTypeAlias;
	/**
	 * mybatis jdbcType
	 */
	private JdbcType jdbcType;
	/**
	 * mybatis typeHandler
	 */
	private Class<? extends TypeHandler<?>> typeHandlerClass;

	public FieldMetaData(EntityMetaData entity, Field field) {
		this.entity = entity;
		this.field = field;
		init();
	}

	private void init() {
		this.primaryKey = field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(PrimaryKey.class);
		this.versionable = field.isAnnotationPresent(Version.class);
		this.javaProperty = field.getName();
		this.javaType = field.getType();
		this.columnName = PersistentRecognizer.getColumnName(field);
		if (field.isAnnotationPresent(Column.class)) {
			Column column = field.getAnnotation(Column.class);
			this.insertable = column.insertable();
			this.updatable = column.updatable();
		}
		if (field.isAnnotationPresent(Formula.class)) {
			Formula formula = field.getAnnotation(Formula.class);
			this.formulable = true;
			this.formula = formula.value();
			this.insertable = false;
			this.updatable = false;
		}
		if (field.isAnnotationPresent(Enumerated.class)) {
			this.enumerated = true;
			this.enumeratedType = field.getAnnotation(Enumerated.class).value();
		}
		resolveJdbc();
		resolveTypeHandler();
	}

	public boolean isTransient() {
		return Modifier.isTransient(field.getModifiers()) ||
			field.isAnnotationPresent(Transient.class);
	}

	private void resolveJdbc() {
		Class<?> fieldType = field.getType();
		if (fieldType.isEnum()) {
			this.jdbcTypeAlias = JdbcType.VARCHAR.name();
			this.jdbcType = JdbcType.VARCHAR;
			if (field.isAnnotationPresent(Enumerated.class)) {
				Enumerated enumerated = field.getAnnotation(Enumerated.class);
				if (enumerated.value() == EnumType.ORDINAL) {
					this.jdbcTypeAlias = JdbcType.INTEGER.name();
					this.jdbcType = JdbcType.INTEGER;
				}
			}
		} else if (String.class.equals(fieldType)) {
			this.jdbcTypeAlias = JdbcType.VARCHAR.name();
			this.jdbcType = JdbcType.VARCHAR;
		} else if (Integer.class.equals(fieldType) || int.class.equals(fieldType)) {
			this.jdbcTypeAlias = JdbcType.INTEGER.name();
			this.jdbcType = JdbcType.INTEGER;
		} else if (Long.class.equals(fieldType) || long.class.equals(fieldType)) {
			this.jdbcTypeAlias = JdbcType.BIGINT.name();
			this.jdbcType = JdbcType.BIGINT;
		} else if (Boolean.class.equals(fieldType) || boolean.class.equals(fieldType)) {
			this.jdbcTypeAlias = JdbcType.BOOLEAN.name();
			this.jdbcType = JdbcType.BOOLEAN;
		} else if (BigDecimal.class.equals(fieldType)) {
			this.jdbcTypeAlias = JdbcType.NUMERIC.name();
			this.jdbcType = JdbcType.NUMERIC;
		} else if (java.util.Date.class.isAssignableFrom(fieldType)) {
			this.jdbcTypeAlias = JdbcType.TIMESTAMP.name();
			this.jdbcType = JdbcType.TIMESTAMP;
			if (field.isAnnotationPresent(Temporal.class)) {
				Temporal temporal = field.getAnnotation(Temporal.class);
				TemporalType temporalType = temporal.value();
				if (temporalType == TemporalType.DATE) {
					this.jdbcTypeAlias = JdbcType.DATE.name();
					this.jdbcType = JdbcType.DATE;
				} else if (temporalType == TemporalType.TIME) {
					this.jdbcTypeAlias = JdbcType.TIME.name();
					this.jdbcType = JdbcType.TIME;
				} else if (temporalType == TemporalType.TIMESTAMP) {
				}
			}
		} else if (Short.class.equals(fieldType) || short.class.equals(fieldType)) {
			this.jdbcTypeAlias = JdbcType.SMALLINT.name();
			this.jdbcType = JdbcType.SMALLINT;
		} else if (Byte.class.equals(fieldType) || byte.class.equals(fieldType)) {
			this.jdbcTypeAlias = JdbcType.TINYINT.name();
			this.jdbcType = JdbcType.TINYINT;
		} else if (Double.class.equals(fieldType) || double.class.equals(fieldType)) {
			this.jdbcTypeAlias = JdbcType.DOUBLE.name();
			this.jdbcType = JdbcType.DOUBLE;
		} else if (Float.class.equals(fieldType) || float.class.equals(fieldType)) {
			this.jdbcTypeAlias = JdbcType.FLOAT.name();
			this.jdbcType = JdbcType.FLOAT;
		} else if (field.isAnnotationPresent(Lob.class)) {
			if (String.class.equals(fieldType)) {
				this.jdbcTypeAlias = JdbcType.CLOB.name();
				this.jdbcType = JdbcType.CLOB;
			} else if (byte[].class.equals(fieldType)) {
				this.jdbcTypeAlias = JdbcType.BLOB.name();
				this.jdbcType = JdbcType.BLOB;
			}
		}
	}

	private void resolveTypeHandler() {
		Class<?> fieldType = field.getType();
		if (fieldType.isEnum()) {
			// this.typeHandlerClass = (Class) BlankableEnumTypeHandler.class;
			this.typeHandlerClass = (Class) DynamicEnumTypeHandler.class;
			if (field.isAnnotationPresent(Enumerated.class)) {
				Enumerated enumerated = field.getAnnotation(Enumerated.class);
				// 设置了value属性
				if (enumerated.value() == EnumType.ORDINAL) {
					// this.typeHandlerClass = (Class) BlankableEnumOrdinalTypeHandler.class;
					this.typeHandlerClass = (Class) DynamicEnumOrdinalTypeHandler.class;
				}
			}
		} else if (fieldType.equals(Boolean.class)) {
			this.typeHandlerClass = DynamicBooleanTypeHandler.class;
		} else if (fieldType.equals(String[].class)) {
			this.typeHandlerClass = StringArrayTypeHandler.class;
		} else if (fieldType.equals(Integer[].class)) {
			this.typeHandlerClass = IntegerArrayTypeHandler.class;
		} else if (fieldType.equals(Long[].class)) {
			this.typeHandlerClass = LongArrayTypeHandler.class;
		} else if (fieldType.equals(BigDecimal[].class)) {
			this.typeHandlerClass = BigDecimalArrayTypeHandler.class;
		} else if (fieldType.equals(Boolean[].class)) {
			this.typeHandlerClass = BooleanArrayTypeHandler.class;
		} else if (fieldType.equals(Double[].class)) {
			this.typeHandlerClass = DoubleArrayTypeHandler.class;
		} else if (fieldType.equals(Float[].class)) {
			this.typeHandlerClass = FloatArrayTypeHandler.class;
		} else if (fieldType.equals(Short[].class)) {
			this.typeHandlerClass = ShortArrayTypeHandler.class;
		} else if (fieldType.equals(Character[].class)) {
			this.typeHandlerClass = CharacterArrayTypeHandler.class;
		} else if (jdbcType != null) {
			switch (jdbcType) {
				case TIMESTAMP:
					this.typeHandlerClass = DynamicTimestampTypeHandler.class;
					break;
				case DATE:
					this.typeHandlerClass = DynamicDateTypeHandler.class;
					break;
				case TIME:
					this.typeHandlerClass = DynamicTimeTypeHandler.class;
					break;
				default:
			}
		}
	}

}

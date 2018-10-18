package io.microvibe.booster.core.base.mybatis.example;

import io.microvibe.booster.core.base.mybatis.builder.BuilderAssistant;
import io.microvibe.booster.core.base.persistence.EntityMetaData;
import io.microvibe.booster.core.base.persistence.FieldMetaData;
import io.microvibe.booster.core.base.persistence.JoinMetaData;
import io.microvibe.booster.core.base.persistence.PersistentRecognizer;
import io.microvibe.booster.core.search.ISymbol;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Qt
 * @since Jul 12, 2018
 */
@SuppressWarnings("All")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter(AccessLevel.PROTECTED)
public class Builder<T> {
	public static final int MAX_DEPTH = 4;
	private Example<T> example;
	private Deque<Set<Example.Criterition>> deque = new ArrayDeque<>();
	private EntityMetaData entityMetaData;
	private String tableAlias;
	private List<String> orderBys = new ArrayList<>();

	protected Builder(Class<T> entityClass, String tableAlias) {
		this.entityMetaData = PersistentRecognizer.entityMetaData(entityClass);
		this.example = createExample();
		if (StringUtils.isBlank(tableAlias)) {
			this.tableAlias = StringUtils.trimToNull(entityMetaData.getTableAlias());
		} else {
			this.tableAlias = StringUtils.trimToNull(tableAlias);
		}
		this.deque.push(this.example.getCriteria());
	}

	protected Example<T> createExample() {
		return new Example<>();
	}

	public Example<T> build() {
		return example;
	}


	public Builder<T> orderBy(String property) {
		return orderBy(property, "");
	}

	public Builder<T> orderByAsc(String property) {
		return orderBy(property, " asc");
	}

	public Builder<T> orderByDesc(String property) {
		return orderBy(property, " desc");
	}

	protected Builder<T> orderBy(String property, String direction) {
		FieldMetaInfo fieldMetaInfo = findFieldMetaInfo(property);
		String column;
		if (fieldMetaInfo.getTableAlias() != null) {
			column = fieldMetaInfo.getTableAlias() + "." + fieldMetaInfo.getFieldMetaData().getColumnName();
		} else {
			column = fieldMetaInfo.getFieldMetaData().getColumnName();
		}

		return _orderBy(column, direction);
	}

	protected Builder<T> _orderBy(String column, String direction) {
		String orderByClause = this.example.getOrderByClause();
		if (StringUtils.isBlank(orderByClause)) {
			orderByClause = column + direction;
		} else {
			orderByClause += ", " + column + direction;
		}
		this.example.setOrderByClause(orderByClause);
		return this;
	}


	public Builder<T> isNull(String property) {
		return addCriterition(property, "is null");
	}

	public Builder<T> isNotNull(String property) {
		return addCriterition(property, "is not null");
	}

	public Builder<T> equalTo(String property, Object value) {
		return addCriterition(property, "=", value);
	}

	public Builder<T> notEqualTo(String property, Object value) {
		return addCriterition(property, "<>", value);
	}

	public Builder<T> greaterThan(String property, Object value) {
		return addCriterition(property, ">", value);
	}

	public Builder<T> lessThan(String property, Object value) {
		return addCriterition(property, "<", value);
	}

	public Builder<T> greaterEqualThan(String property, Object value) {
		return addCriterition(property, ">=", value);
	}

	public Builder<T> lessEqualThan(String property, Object value) {
		return addCriterition(property, "<=", value);
	}

	public Builder<T> in(String property, Iterable<Object> value) {
		return addCriterition(property, "in", value);
	}

	public Builder<T> notIn(String property, Iterable<Object> value) {
		return addCriterition(property, "not in", value);
	}

	public Builder<T> between(String property, Object value1, Object value2) {
		return addCriterition(property, "between", value1, value2);
	}

	public Builder<T> notBetween(String property, Object value1, Object value2) {
		return addCriterition(property, "not between", value1, value2);
	}

	public Builder<T> like(String property, Object value) {
		return addCriterition(property, "like", value);
	}

	public Builder<T> notLike(String property, Object value) {
		return addCriterition(property, "not like", value);
	}


	public Builder<T> protoSql(String property, String protoSql) {
		Example.Criterition criterition = new Example.Criterition();
		FieldMetaInfo fieldMetaInfo = findFieldMetaInfo(property);
		if (fieldMetaInfo.getTableAlias() != null) {
			criterition.setColumn(fieldMetaInfo.getTableAlias()
				+ "." + fieldMetaInfo.getFieldMetaData().getColumnName());
		} else {
			criterition.setColumn(fieldMetaInfo.getFieldMetaData().getColumnName());
		}
		criterition.setProto(true);
		Pattern p = Pattern.compile("(\\\\*)\\{\\}");
		Matcher m = p.matcher(protoSql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			if ((m.group(1).length() & 1) == 0) {
				m.appendReplacement(sb, m.group(1));
				sb.append(criterition.getColumn());
			} else {
				m.appendReplacement(sb, m.group());
			}
		}
		m.appendTail(sb);
		criterition.setProtoSql(sb.toString());
		return addCriterition(criterition);
	}

	public Builder<T> where(String property, ISymbol symbol, Object value) {
		if (symbol.isNoValue()) {
			return addCriterition(property, symbol.symbol());
		} else if (symbol.isSingleValue()) {
			return addCriterition(property, symbol.symbol(), value);
		} else if (symbol.isDoubleValue()) {
			if (value instanceof Iterable) {
				Iterator iter = ((Iterable) value).iterator();
				return addCriterition(property, symbol.symbol(), iter.next(), iter.next());
			} else if (value.getClass().isArray() && Array.getLength(value) == 2) {
				return addCriterition(property, symbol.symbol(), Array.get(value, 0), Array.get(value, 1));
			} else {
				throw new IllegalArgumentException(property);
			}
		} else if (symbol.isListValue()) {
			if (value instanceof Iterable) {
				return addCriterition(property, symbol.symbol(), ((Iterable) value));
			} else if (value.getClass().isArray()) {
				int len = Array.getLength(value);
				List<Object> list = new ArrayList<>();
				for (int i = 0; i < len; i++) {
					list.add(Array.get(value, i));
				}
				return addCriterition(property, symbol.symbol(), list);
			} else {
				throw new IllegalArgumentException(property);
			}
		} else {
			throw new IllegalArgumentException(property);
		}
	}

	private Builder<T> addCriterition(String property, String symbol) {
		Example.Criterition criterition = newCriterition(property, symbol);
		criterition.setNoValue(true);
		return addCriterition(criterition);
	}

	private Builder<T> addCriterition(String property, String symbol, Object value) {
		Example.Criterition criterition = newCriterition(property, symbol);
		criterition.setSingleValue(true);
		criterition.setValue(value);
		return addCriterition(criterition);
	}

	private Builder<T> addCriterition(String property, String symbol, Iterable<Object> value) {
		Example.Criterition criterition = newCriterition(property, symbol);
		criterition.setListValue(true);
		criterition.setValue(value);
		return addCriterition(criterition);
	}

	private Builder<T> addCriterition(String property, String symbol, Object value, Object secondValue) {
		Example.Criterition criterition = newCriterition(property, symbol);
		criterition.setBetweenValue(true);
		criterition.setValue(value);
		criterition.setSecondValue(secondValue);
		return addCriterition(criterition);
	}

	private Example.Criterition newCriterition(String property, String symbol) {
		Example.Criterition criterition = new Example.Criterition();
		FieldMetaInfo fieldMetaInfo = findFieldMetaInfo(property);
		criterition.setColumn(BuilderAssistant.instance().columnExpression(
			fieldMetaInfo.getTableName(), fieldMetaInfo.getTableAlias(), fieldMetaInfo.getFieldMetaData()));
		/*if (fieldMetaData.isFormulable()) {

		} else {
			if (tableAlias != null) {
				criterition.setColumn(tableAlias + "." + fieldMetaData.getColumnName());
			} else {
				criterition.setColumn(fieldMetaData.getColumnName());
			}
		}*/
		criterition.setSymbol(symbol);
		return criterition;
	}

	private Builder<T> addCriterition(Example.Criterition criterition) {
		deque.peek().add(criterition);
		return this;
	}

	private Builder<T> subCriteria(String conj) {
		Example.Criterition criterition = new Example.Criterition();
		criterition.setConj(conj);
		criterition.setCriteria(new LinkedHashSet<>());
		if (deque.size() >= MAX_DEPTH) {//支持层数限制
			throw new IllegalStateException("depth overflow : " + deque.size());
		}
		deque.peek().add(criterition);
		deque.push(criterition.getCriteria());
		return this;
	}

	public Builder<T> end() {
		if (deque.size() == 1) {//顶层不可移除
			throw new IllegalStateException();
		}
		deque.pop();
		return this;
	}

	public Builder<T> and() {
		return subCriteria("and");
	}

	public Builder<T> or() {
		return subCriteria("or");
	}

	private FieldMetaInfo findFieldMetaInfo(String property) {
		FieldMetaInfo info = new FieldMetaInfo();
		int i = property.indexOf('.');
		if (i > 0) {
			String sub = property.substring(0, i);
			String subKey = property.substring(i + 1);
			JoinMetaData joinMetaData = entityMetaData.getJoinMetaData(sub);
			if (joinMetaData != null) {
				EntityMetaData tableMetaData = joinMetaData.getTableMetaData();
				info.setFieldMetaData(tableMetaData.getFieldMetaData(subKey));
				info.setTableAlias(sub);
				info.setTableName(tableMetaData.getTableName());
			} else {
				PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(entityMetaData.getEntityClass(), sub);
				if (propertyDescriptor != null) {
					Class<?> propertyType = propertyDescriptor.getPropertyType();
					EntityMetaData tableMetaData = PersistentRecognizer.entityMetaData(propertyType);
					info.setFieldMetaData(tableMetaData.getFieldMetaData(subKey));
					info.setTableAlias(sub);
					info.setTableName(tableMetaData.getTableName());
				}
			}
		} else {
			FieldMetaData fieldMetaData = entityMetaData.getFieldMetaData(property);
			info.setFieldMetaData(fieldMetaData);
			info.setTableAlias(tableAlias);
			info.setTableName(entityMetaData.getTableName());
		}
		if (info.getFieldMetaData() == null) {
			throw new IllegalArgumentException(property);
		}
		return info;
	}

	@Data
	static class FieldMetaInfo {

		private String tableName;
		private String tableAlias;
		private FieldMetaData fieldMetaData;
	}
}

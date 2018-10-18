package io.microvibe.booster.core.base.search.builder.support;

import io.microvibe.booster.core.base.search.builder.QLParameter;
import io.microvibe.booster.core.search.ISymbol;
import io.microvibe.booster.core.search.SearchOper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Condition implements Where {

	@Setter
	@Getter
	private Class<?> entityClass;
	@Setter
	@Getter
	private String alias;
	@Setter
	@Getter
	private String key;
	@Setter
	@Getter
	private ISymbol op;
	@Setter
	@Getter
	private Optional<Object> val;
	@Setter(AccessLevel.PRIVATE)
	private boolean converted = false;
	private List<QLParameter> bindedValues = new ArrayList<>();
	private List<String> bindedKeys = new ArrayList<>();
	private Where proxy;

	public Condition() {
	}

	public Condition(String key) {
		this(key, SearchOper.proto);
	}

	public Condition(String key, ISymbol op) {
		this();
		this.key = key;
		this.op = op;
		this.val = null;
	}

	public Condition(String key, ISymbol op, Object... val) {
		this();
		this.key = key;
		this.op = op;
		this.val = null;
		if (val.length == 0) {
			this.val = null;
		} else if (val.length == 1) {
			this.val = Optional.ofNullable(val[0]);
		} else {
			this.val = Optional.ofNullable(val);
		}
	}

	public List<QLParameter> bindedValues() {
		if (proxy != null) {
			return proxy.bindedValues();
		}
		return bindedValues;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public String statement() {
		repair();
		if (proxy != null) {
			return proxy.statement();
		}
		StringBuilder builder = new StringBuilder();
		if (StringUtils.isNotBlank(alias)) {
			builder.append(alias).append(".");
		}
		builder.append(key);
		if (StringUtils.isNotBlank(op.symbol())) {
			builder.append(" ").append(op.symbol());
		}

		if (op != SearchOper.proto /*如是原生SQL操作符则key就是SQL本身*/
			&& !op.isNoValue() /*如单目操作符则不需要value作绑定参数*/
			&& val != null /* 须有绑定参数*/) {
			String bindedKey = BindedKeys.nextBindedKey();
			builder.append(" :").append(bindedKey);
			bindedKeys.add(bindedKey);
		}

		if (val != null && val.isPresent()) {
			Object value = val.get();
			if (bindedKeys.size() == 1) {
				bindedValues.add(new QLParameter(bindedKeys.get(0), value));
			} else if (bindedKeys.size() > 1) {
				Iterator<Object> iter;
				if (value.getClass().isArray()) {
					iter = CollectionUtils.arrayToList(value).iterator();
				} else if (value instanceof Collection) {
					iter = ((Collection) value).iterator();
				} else {
					iter = Arrays.asList(value).iterator();
				}
				for (String bindedKey : bindedKeys) {
					if (iter.hasNext()) {
						bindedValues.add(new QLParameter(bindedKey, iter.next()));
					} else {
						bindedValues.add(new QLParameter(bindedKey, null));
					}
				}
			}
		} else {
			for (String bindedKey : bindedKeys) {
				bindedValues.add(new QLParameter(bindedKey, null));
			}
		}

		return builder.toString();
	}

	@SuppressWarnings("rawtypes")
	private void repair() {
		key = StringUtils.trimToNull(key);
		if (key == null) {
			throw new IllegalArgumentException("search key is empty!");
		}
		proxy = null;
		bindedKeys.clear();
		bindedValues.clear();
		if (op == SearchOper.proto) {// 原生sql
			alias = null;
			entityClass = null;
			Pattern p = Pattern.compile("(\\\\*)\\{\\}");
			Matcher m = p.matcher(key);
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				if ((m.group(1).length() & 1) == 0) {
					m.appendReplacement(sb, m.group(1));
					String bindedKey = BindedKeys.nextBindedKey();
					bindedKeys.add(bindedKey);
					sb.append(":").append(bindedKey);
				} else {
					m.appendReplacement(sb, m.group());
				}
			}
			m.appendTail(sb);
			key = sb.toString();
		} else if (!converted) {// 需要作类型转换
			List<Table> tables = Table.getContext();
			if (tables != null) {
				if (alias == null) {
					int idx = key.indexOf(".");
					if (idx > 0) {
						alias = key.substring(0, idx);
						key = key.substring(idx + 1);
					}
				}
				if (alias != null) {
					for (Table table : tables) {
						if (alias.equals(table.getAlias())) {
							if (entityClass != null) {
								if (entityClass != table.getEntityClass()) {
									throw new IllegalArgumentException("entity class not match the alias!");
								}
							} else {
								entityClass = table.getEntityClass();
							}
							break;
						}
					}
				} else {
					for (Table table : tables) {
						if (entityClass == null) {
							if (table.getEntityClass() != null && BeanUtils.getPropertyDescriptor(table
								.getEntityClass(), key) != null) {
								entityClass = table.getEntityClass();
								alias = table.getAlias();
								break;
							}
						} else if (entityClass == table.getEntityClass()) {
							alias = table.getAlias();
							break;
						}
					}
				}
				if (!op.isNoValue() && val != null && val.isPresent()) {
					// 非空值时需要转换类型为实体所需类型
					if (entityClass != null) {
						val = Conversions.convert(entityClass, key, val);
//                    } else {
//                        log.debug("cannot find the matched entity class for key: {}", key);
					}
				}
			}

			// 处理 `in` 条件
			if (op == SearchOper.in || op == SearchOper.notIn) {
				Object value = val.get();
				Class<? extends Object> vClass = value.getClass();
				if (value instanceof Collection) {
					if (((Collection) value).size() > MAX_COUNT_OF_IN) {
						QLStructSupport struct = new QLStructSupport();
						this.proxy = new And(struct);
						Iterator iter = ((Collection) value).iterator();
						while (iter.hasNext()) {
							int i = 0;
							List<Object> list = new ArrayList<>();
							while (iter.hasNext()) {
								Object next = iter.next();
								list.add(next);
								i++;
								if (i >= MAX_COUNT_OF_IN) {
									break;
								}
							}
							if (op == SearchOper.in) {
								struct.OR().WHERE(newInCondition(list)).END();
							} else {
								struct.WHERE(newNotInCondition(list));
							}
						}
					}
				} else if (vClass.isArray()) {
					int len = Array.getLength(value);
					if (len > MAX_COUNT_OF_IN) {
						QLStructSupport struct = new QLStructSupport();
						this.proxy = new And(struct);
						for (int i = 0; i < len; i++) {
							List<Object> list = new ArrayList<>();
							for (int j = 0; j < 1000 && i + j < len; j++) {
								list.add(Array.get(value, i + j));
							}
							if (op == SearchOper.in) {
								struct.OR().WHERE(newInCondition(list)).END();
							} else {
								struct.WHERE(newNotInCondition(list));
							}
						}
					}
				} else {
					throw new IllegalArgumentException("值类型不适用于`in`条件语句: " + vClass);
				}
			} else if (op == SearchOper.like || op == SearchOper.notLike) {
				// 处理 `like` 条件
				val = Optional.of("%" + val.get().toString() + "%");
			} else if (op == SearchOper.prefixLike || op == SearchOper.prefixNotLike) {
				// 处理 `like` 条件
				val = Optional.of("%" + val.get().toString());
			} else if (op == SearchOper.suffixLike || op == SearchOper.suffixNotLike) {
				// 处理 `like` 条件
				val = Optional.of(val.get().toString() + "%");
			}
		}
	}

	private Condition newInCondition(List<Object> list) {
		Condition cond = new Condition();
		cond.setConverted(true);
		cond.setAlias(alias);
		cond.setEntityClass(entityClass);
		cond.setKey(key);
		cond.setOp(SearchOper.in);
		cond.setVal(Optional.of(list));
		return cond;
	}

	private Condition newNotInCondition(List<Object> list) {
		Condition cond = new Condition();
		cond.setConverted(true);
		cond.setAlias(alias);
		cond.setEntityClass(entityClass);
		cond.setKey(key);
		cond.setOp(SearchOper.notIn);
		cond.setVal(Optional.of(list));
		return cond;
	}
}

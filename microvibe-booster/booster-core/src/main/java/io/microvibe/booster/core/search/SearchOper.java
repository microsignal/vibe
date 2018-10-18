package io.microvibe.booster.core.search;

import lombok.Getter;

import java.util.function.Function;

public enum SearchOper implements ISymbol {
	proto("原生", null, SearchValueType.NOTHING),

	isNull("空", "is null", SearchValueType.NOTHING),
	isNotNull("非空", "is not null", SearchValueType.NOTHING),

	eq("等于", "=", SearchValueType.SINGLE),
	ne("不等于", "!=", SearchValueType.SINGLE),

	between("介于", "between", SearchValueType.DOUBLE),
	notBetween("不介于", "not between", SearchValueType.DOUBLE),

	gt("大于", ">", SearchValueType.SINGLE),
	ge("大于等于", ">=", SearchValueType.SINGLE),
	gte("大于等于", ">=", SearchValueType.SINGLE),

	lt("小于", "<", SearchValueType.SINGLE),
	lte("小于等于", "<=", SearchValueType.SINGLE),
	le("小于等于", "<=", SearchValueType.SINGLE),

	in("包含", "in", SearchValueType.LIST),
	notIn("不包含", "not in", SearchValueType.LIST),

	like("模糊匹配", "like", SearchValueType.SINGLE, Searches::quoteWildcard),
	notLike("不匹配", "not like", SearchValueType.SINGLE, Searches::quoteWildcard),

	preLike("前模糊后匹配", "like", SearchValueType.SINGLE, Searches::prependWildcard),
	notPreLike("前模糊后匹配", "like", SearchValueType.SINGLE, Searches::prependWildcard),
	sufLike("后模糊前匹配", "like", SearchValueType.SINGLE, Searches::appendWildcard),
	notSufLike("后模糊前匹配", "like", SearchValueType.SINGLE, Searches::appendWildcard),

	prefixLike("前模糊后匹配", "like", SearchValueType.SINGLE, Searches::prependWildcard),
	prefixNotLike("前模糊后不匹配", "not like", SearchValueType.SINGLE, Searches::prependWildcard),
	suffixLike("后模糊前匹配", "like", SearchValueType.SINGLE, Searches::appendWildcard),
	suffixNotLike("后模糊前不匹配", "not like", SearchValueType.SINGLE, Searches::appendWildcard),
	//
	;
	private final String intro;
	private final String symbol;
	private final Function<Object, Object> quoteFunc;
	private SearchValueType valueType = SearchValueType.SINGLE;

	private SearchOper(String intro, String symbol, SearchValueType valueType, Function<Object, Object> quoteFunc) {
		this.intro = intro;
		this.symbol = symbol;
		this.valueType = valueType;
		this.quoteFunc = quoteFunc;
	}
	private SearchOper(String intro, String symbol) {
		this(intro, symbol, SearchValueType.SINGLE , null);
	}

	private SearchOper(String intro, String symbol, SearchValueType valueType) {
		this(intro, symbol, valueType, null);
	}

	@Override
	public Object repair(Object searchValue) {
		if (isNoValue()) {
			return "$#%#$";
		}
		if (quoteFunc == null) {
			return searchValue;
		}
		return quoteFunc.apply(searchValue);
	}

	@Override
	public String symbol() {
		return symbol;
	}

	public SearchValueType valueType() {
		return valueType;
	}

	public String intro() {
		return intro;
	}

}

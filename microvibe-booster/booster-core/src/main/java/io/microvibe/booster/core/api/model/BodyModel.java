package io.microvibe.booster.core.api.model;

import io.microvibe.booster.core.search.ISymbol;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * request:
 * {
 *   search:[
 *     {key:'id',op:'eq',val:'1'},
 *     {key:'name',op:'like',val:'xx'},
 *     ...
 *   ]
 *   sort:[
 *     {key:'createDate',dir:'asc'},
 *     {key:'updateDate',dir:'desc'}
 *   ],
 *   limit:{
 *     ps:10,pn:1
 *   }
 * }
 *
 * response:
 * {
 *   head:{
 *     code:0,
 *     success:true,
 *     message:'ok'
 *   },
 *   body:{
 *     list:[
 *       {k1:123,k2:'abc',...},
 *       {k1:123,k2:'abc',...},
 *       ...
 *     ],
 *     total:1000,
 *     sortable:['k1','k2',...],
 *     limit:{
 *       ps:10,pn:1
 *     }
 *   }
 * }
 * </pre>
 */
public interface BodyModel extends java.io.Serializable, SearchableProvider, IJSONObject, Map<String, Object> {

	/**
	 * 返回本对象克隆体
	 *
	 * @return 本对象克隆体
	 */
	BodyModel clone();

	// region body-search

	/**
	 * 返回查询条件对象
	 *
	 * @return 查询条件对象
	 */
	List<SearchModel> getSearches();

	List<SearchModel> getSearches(final String searchKey);

	SearchModel getSearch(final String searchKey, ISymbol op);

	/**
	 * 添加指定的查询条件
	 *
	 * @return 是否添加成功
	 */
	boolean addSearch(String searchKey, ISymbol op, Object searchValue);

	/**
	 * 清除指定的查询条件
	 *
	 * @return 是否清除了任意条件
	 */
	boolean clearSearch(String searchKey, ISymbol op);

	/**
	 * 清除指定的查询条件
	 *
	 * @return 是否清除了任意条件
	 */
	boolean clearSearch(String searchKey);

	/**
	 * 清除所有查询条件
	 */
	void clearSearch();

	void addSearch(SearchModel... searchModel);

	void setSearch(List<SearchModel> searchModelList);

	// endregion

	// region body-limit

	/**
	 * 返回分页参数对象
	 *
	 * @return 分页参数对象
	 */
	LimitModel getLimit();

	void setLimit(LimitModel limit);

	void setLimit(int pageNumber, int pageSize);

	void clearLimit();

	// endregion

	// region body-sort

	/**
	 * 返回排序参数对象
	 *
	 * @return 排序参数对象
	 */
	List<SortModel> getSorts();

	boolean addSort(String sortKey, Sort.Direction sortDirection);

	boolean clearSort(String sortKey);

	void clearSort();

	void addSort(SortModel... sort);

	void setSort(List<SortModel> sort);

	// endregion

	// region body-sortable

	/**
	 * 返回可排序字段列表
	 *
	 * @return 可排序字段列表
	 */
	List<String> getSortable();

	void setSortable(List<String> sortable);

	void addSortable(String... sortable);

	// endregion

	// region body-data

	/**
	 * 返回数据对象列表
	 *
	 * @return 数据对象列表
	 */
	List<?> getDataList();

	void setDataList(List<?> list);

	void addDataList(Object... data);

	/**
	 * 返回查询数据总量
	 *
	 * @return 查询数据总量
	 */
	long getDataTotal();

	void setDataTotal(long total);

	// endregion


}

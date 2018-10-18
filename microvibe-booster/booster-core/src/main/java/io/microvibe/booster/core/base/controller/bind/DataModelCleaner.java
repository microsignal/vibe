package io.microvibe.booster.core.base.controller.bind;

import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.core.api.model.BodyModel;
import io.microvibe.booster.core.api.model.SearchModel;
import io.microvibe.booster.core.api.model.SortModel;
import io.microvibe.booster.core.api.tools.DataKeyExtracter;
import io.microvibe.booster.core.search.ISymbol;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class DataModelCleaner {

	public static void cleanKeys(BodyModel body, DataKeyExtracter<?> keyExtracter) {
		List<SearchModel> searchModelList = body.getSearches();
		cleanUnnecessarySearchKeys(keyExtracter, searchModelList);
		List<SortModel> sortModelList = body.getSorts();
		cleanUnnecessarySortKeys(keyExtracter, sortModelList);
	}

	private static void cleanUnnecessarySortKeys(DataKeyExtracter<?> keyExtracter, List<SortModel> sortModelList) {
		if (sortModelList != null && keyExtracter != null) {
			for (Iterator<SortModel> iter = sortModelList.iterator(); iter.hasNext(); ) {
				SortModel sortModel = iter.next();
				String key = sortModel.getSortKey();
				if (keyExtracter.isExcluded(key) || !keyExtracter.isIncluded(key)) {
					iter.remove();// 移除需要排除的查询条件
				} else {
					sortModel.setSortKey(keyExtracter.getMappingKey(key));
				}
			}
		}
	}

	public static List<SearchModel> cleanUnnecessarySearchKeys(DataKeyExtracter<?> keyExtracter, List<SearchModel> searchModelList) {
		if (searchModelList != null) {
			for (Iterator<SearchModel> iter = searchModelList.iterator(); iter.hasNext(); ) {
				SearchModel searchModel = iter.next();
				SearchModel.Conj conj = searchModel.getConj();
				if (conj == null) {
					ISymbol op = searchModel.getOp();
					if (!op.isNoValue()) {
						Object val = searchModel.getVal();
						if (val == null || "".equals(val.toString().trim())) {
							iter.remove();// 移除空查询条件
						}
					}
					if (keyExtracter != null) {
						String key = searchModel.getKey();
						if (keyExtracter.isExcluded(key) || !keyExtracter.isIncluded(key)) {
							iter.remove();// 移除需要排除的查询条件
						} else {
							searchModel.setKey(keyExtracter.getMappingKey(key));
						}
					}
				} else {
					List<SearchModel> subSearches = searchModel.getSearches();
					subSearches = cleanUnnecessarySearchKeys(keyExtracter, subSearches);// 递归清理子条件组
					if (subSearches == null || subSearches.size() == 0) {//已无子条件组
						iter.remove();
					}
				}
			}
		}
		return searchModelList;
	}

	public static void cleanEmptyValues(Map<String, Object> body) {
		Set<Map.Entry<String, Object>> entries = body.entrySet();
		for (Iterator<Map.Entry<String, Object>> iter = entries.iterator(); iter.hasNext(); ) {
			Map.Entry<String, Object> entry = iter.next();
			Object value = entry.getValue();
			if (value == null) {
				iter.remove();
			} else if (value instanceof CharSequence) {
				if (StringUtils.isBlank((CharSequence) value)) {
					iter.remove();
				}
			} else if (value instanceof Map) {
				cleanEmptyValues((Map<String, Object>) value);
			} else if (value instanceof Collection) {
				cleanEmptyValues((Collection) value);
			} else if (value.getClass().isArray()) {
				cleanEmptyValues(CollectionUtils.arrayToList(value));
			}
		}
	}

	private static void cleanEmptyValues(Collection value) {
		value.forEach(o -> {
			if (o instanceof Map) {
				cleanEmptyValues((Map<String, Object>) o);
			} else if (o instanceof Collection) {
				cleanEmptyValues((Collection) o);
			}
		});
	}
}

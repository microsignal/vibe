package io.microvibe.booster.core.base.service;

import io.microvibe.booster.core.base.mapper.PlainSqlMapper;
import io.microvibe.booster.core.base.repository.support.WrappedPageImpl;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @since Jul 31, 2018
 */
@Service
public class PlainSqlService {

	@Autowired
	PlainSqlMapper plainSqlMapper;

	public long count(Map<String, Object> param) {
		return plainSqlMapper.count(param);
	}

	public Map<String, Object> get(Map<String, Object> param) {
		return plainSqlMapper.get(param);
	}

	public List<Map<String, Object>> select(Map<String, Object> param) {
		return plainSqlMapper.select(param);
	}

	public Page<Map<String, Object>> selectByPage(Map<String, Object> param, Pageable pageable) {
		PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize());
		Sort sort = pageable.getSort();
		orderBy(sort);
		List<Map<String, Object>> list = plainSqlMapper.select(param);
		if (list instanceof com.github.pagehelper.Page) {
			long total = ((com.github.pagehelper.Page<Map<String, Object>>) list).getTotal();
			return new WrappedPageImpl<>(list, pageable, total);
		} else {
			return new WrappedPageImpl<>(list);
		}
	}

	public List<Map<String, Object>> select(Map<String, Object> param, Pageable pageable) {
		RowBounds rowBounds = new RowBounds(pageable.getOffset(), pageable.getPageSize());
		List<Map<String, Object>> list = plainSqlMapper.select(param, rowBounds);
		return list;
	}

	public int update(Map<String, Object> param) {
		return plainSqlMapper.update(param);
	}

	public int delete(Map<String, Object> param) {
		return plainSqlMapper.delete(param);
	}

	public int insert(Map<String, Object> param) {
		return plainSqlMapper.insert(param);
	}

	private void orderBy(Sort sort) {
		if (sort != null) {
			Iterator<Sort.Order> iter = sort.iterator();
			StringBuilder orderby = new StringBuilder();
			while (iter.hasNext()) {
				Sort.Order order = iter.next();
				if (orderby.length() > 0) {
					orderby.append(", ");
				}
				String property = order.getProperty();
				orderby.append(property).append(" ")
					.append(order.getDirection().name().toLowerCase());
			}
			if (orderby.length() > 0) {
				PageHelper.orderBy(orderby.toString());
			}
		}
	}

}

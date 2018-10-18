package io.microvibe.booster.commons.utils.property;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/12/27.
 */
public class PageUtil {

	private PageUtil() {
	}

	public static <T> Page fitterPageWith(Page<T> page, Pageable pageable, Class<T> clazz, String... includes) {
		List<T> collect = page.getContent().stream()
			.map(t -> {
				try {
					return (T) PropertyUtil.mergeWith(clazz.newInstance(), t, includes);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				return null;
			})
			.collect(Collectors.toList());
		return new PageImpl<>(collect, pageable, page.getTotalElements());
	}

	public static <T> Page fitterPageWithout(Page<T> page, Pageable pageable, Class<T> clazz, String... excludes) {
		List<T> collect = page.getContent().stream()
			.map(t -> {
				try {
					return (T) PropertyUtil.mergeWithout(clazz.newInstance(), t, excludes);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				return null;
			})
			.collect(Collectors.toList());
		return new PageImpl<>(collect, pageable, page.getTotalElements());
	}
}

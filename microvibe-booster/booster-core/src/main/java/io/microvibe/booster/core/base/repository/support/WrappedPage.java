package io.microvibe.booster.core.base.repository.support;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;

/**
 * 适配 <b>spring-data-commons:1.6.1.RELEASE</b> 的<b>Page</b>接口方法
 *
 * <ul>
 * <li>{@link #hasPreviousPage()}</li>
 * <li>{@link #isFirstPage()}</li>
 * <li>{@link #hasNextPage()}</li>
 * <li>{@link #isLastPage()}</li>
 * </ul>
 *
 * @author Qt
 * @version 1.0
 * @since 2017/08/30
 */
public interface WrappedPage<T> {
	/**
	 * @see Page#getNumber()
	 */
	int getNumber();

	/**
	 * @see Page#getSize()
	 */
	int getSize();

	/**
	 * @see Page#getTotalPages()
	 */
	int getTotalPages();

	/**
	 * @see Page#getNumberOfElements()
	 */
	int getNumberOfElements();

	/**
	 * @see Page#getTotalElements()
	 */
	long getTotalElements();

	/**
	 * @see Page#hasPrevious()
	 */
	boolean hasPreviousPage();

	/**
	 * @see Page#isFirst()
	 */
	boolean isFirstPage();

	/**
	 * @see Page#hasNext()
	 */
	boolean hasNextPage();

	/**
	 * @see Page#isLast()
	 */
	boolean isLastPage();

	/**
	 * @see Page#nextPageable()
	 */
	Pageable nextPageable();

	/**
	 * @see Page#previousPageable()
	 */
	Pageable previousPageable();

	/**
	 * @see Page#iterator()
	 */
	Iterator<T> iterator();

	/**
	 * @see Page#getContent()
	 */
	List<T> getContent();

	/**
	 * @see Page#hasContent()
	 */
	boolean hasContent();

	/**
	 * @see Page#getSort()
	 */
	Sort getSort();

}

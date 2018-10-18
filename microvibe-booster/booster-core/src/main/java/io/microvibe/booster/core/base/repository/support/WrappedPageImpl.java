package io.microvibe.booster.core.base.repository.support;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * {@inheritDoc}
 *
 * @param <T>
 * @author Qt
 */
public class WrappedPageImpl<T> extends PageImpl<T> implements WrappedPage<T> {

	private static final long serialVersionUID = 1L;

	public WrappedPageImpl(List<T> content, Pageable pageable, long total) {
		super(content, repair(pageable, total), total);
	}

	public WrappedPageImpl(List<T> content) {
		super(content);
	}

	/**
	 * 根据总数, 修复分页参数的页号错误
	 */
	public static Pageable repair(Pageable pageable, long total) {
		if (pageable != null) {
			if (total == 0) {
				pageable = new PageRequest(0, pageable.getPageSize(), pageable.getSort());
			} else if (pageable.getOffset() >= total) {
				//页号超限
				int size = pageable.getPageSize();
				if (size <= 0) {
					size = 10;
				}
				int page = (int) (Math.max(0, total - 1) / size);
				pageable = new PageRequest(page, size, pageable.getSort());
			}
		}
		return pageable;
	}

	@Override
	public boolean hasPreviousPage() {
		return super.hasPrevious();
	}

	@Override
	public boolean isFirstPage() {
		return super.isFirst();
	}

	@Override
	public boolean hasNextPage() {
		return super.hasNext();
	}

	@Override
	public boolean isLastPage() {
		return super.isLast();
	}

}

package io.microvibe.booster.core.api.model;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface SearchableProvider {

	Sort getSort();

	Pageable getPageable();

	Pageable getPageableWithoutSort();

}

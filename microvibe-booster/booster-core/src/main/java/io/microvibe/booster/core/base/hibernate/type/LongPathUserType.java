package io.microvibe.booster.core.base.hibernate.type;

import java.util.ArrayList;

/**
 * @author Qt
 * @see CollectionToStringUserType
 * @since Nov 13, 2017
 */
public class LongPathUserType extends CollectionToStringUserType {
	private static final long serialVersionUID = 1L;

	public LongPathUserType() {
		setCollectionType(ArrayList.class);
		setElementType(Long.class);
		setSeparator("/");
	}
}

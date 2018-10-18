package io.microvibe.util.castor.support;


@SuppressWarnings({ "rawtypes", "unchecked" })
public class AnyEnumCastor<T extends Enum> extends AbstractCastor<T> {

	public AnyEnumCastor(Class<T> type) {
		super(type);
	}

	@Override
	public final T cast(Object orig) {
		if (orig == null) {
			return null;
		}
		if (type.getClass() == orig.getClass()) {
			return (T) orig;
		}
		return (T) Enum.valueOf(type, orig.toString());
	}

}

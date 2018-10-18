package io.microvibe.castor;

public class MarshallerEnv {

	private static boolean attributePrefer = Constants.MARSHAL_ATTRIBUTE_PRIOR;

	public static void resetAttributePrefer() {
		attributePrefer = Constants.MARSHAL_ATTRIBUTE_PRIOR;
	}

	public static void setAttributePrefer(boolean attributePrefer) {
		MarshallerEnv.attributePrefer = attributePrefer;
	}

	public static boolean isAttributePrefer() {
		return attributePrefer;
	}
}

package io.microvibe.booster.core.lang.velocity;

import io.microvibe.booster.core.lang.LocalDataBinding;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

public class VelocityContextLocal extends LocalDataBinding {

	public static Context getContext() {
		Context context = new VelocityContext(LocalDataBinding.getBindings());
		return context;
	}

}

package io.microvibe.booster.core.base.resource.annotation;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResourceIdentities {

	ResourceIdentity[] value();

}

package io.microvibe.booster.core.base.controller.annotation;

import java.lang.annotation.*;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataKeys {

    DataKey[] value();

}

package io.microvibe.booster.core.base.entity;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
public @interface EntryType {

    String value();
}

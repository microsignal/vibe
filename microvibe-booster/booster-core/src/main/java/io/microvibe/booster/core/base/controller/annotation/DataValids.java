package io.microvibe.booster.core.base.controller.annotation;

import java.lang.annotation.*;

/**
 * @author Qt
 * @since Jun 30, 2018
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataValids {

	DataValid[] value();

}


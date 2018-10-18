package io.microvibe.booster.core.base.datasource;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MemoryDataSource {
	String ID = "memory";

}

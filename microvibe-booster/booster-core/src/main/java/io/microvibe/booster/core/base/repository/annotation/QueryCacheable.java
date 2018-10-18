package io.microvibe.booster.core.base.repository.annotation;

import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.lang.annotation.*;

@QueryHints(@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true"))
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryCacheable {

}

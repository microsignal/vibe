package io.microvibe.booster.core.base.controller.annotation;

import java.lang.annotation.*;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(DataKeys.class)
public @interface DataKey {

    String[] mappingFrom() default {};

    String[] mappingTo() default {};

    Class<?>[] mappingAllCamelToUnderline() default {};

    Class<?>[] mappingAllUnderlineToCamel() default {};

    String[] include() default {};

    Class<?>[] includeAll() default {};

    Class<?>[] includeAllUnderlineFormat() default {};

    String[] exclude() default {};

    Class<?>[] excludeAll() default {};

    Class<?>[] excludeAllUnderlineFormat() default {};

}

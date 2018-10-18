package io.microvibe.booster.core.base.annotation;

import java.lang.annotation.*;

/**
 * 查找注解的字段作为BaseService/BaseRepository数据
 * 即
 * 1、查找对象中的注解了@BaseComponent的对象
 * 2、
 * 调用BaseCRUDController.setJpaBaseService 设置BaseService
 * 调用BaseService.setBaseRepository 设置BaseRepository
 *
 * @author Qt
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BaseComponent {

}

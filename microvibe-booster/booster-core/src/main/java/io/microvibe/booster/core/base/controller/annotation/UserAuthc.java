package io.microvibe.booster.core.base.controller.annotation;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;

import java.lang.annotation.*;

/**
 *
 * 定义Controler类型或方法映射到的资源或操作的权限名称, 以便运行期由拦截器完成权限校验
 *
 * @see RequiresPermissions
 * @see RequiresRoles
 * @see RequiresAuthentication
 * @see RequiresUser
 *
 * @since Oct 19, 2017
 * @author Qt
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UserAuthc {

    /**
     * 是否需要校验登录权限
     */
    boolean value() default true;

}

package com.zp.apt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 给接口自动绑定实现类，实现类必须是无参构造
 * <p>
 * 通过ApiFinder.findApi()来查找
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ApiImpl {

    /**
     * 默认false,标识一个接口只有一个实现类，通过ApiFinder.findApi()自动找到实现类
     * <p>
     * true,标识一个接口有多个实现类,主要用于初始化场景,功能暂未实现
     *
     * @return 当前接口的实现类是否多实现
     */
    boolean multi() default false;
}

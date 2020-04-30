package com.zhoupu.router.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 给接口定义默认实现类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ApiList {
    /**
     * @return 接口实现类的列表
     */
    Class value();
}

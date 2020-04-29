package com.zhoupu.router.api;

import java.util.Map;

/**
 * Created by sdl on 2020/4/29
 * 用于定义一个key对应一个class的全路径，由于impl类可能是package的，无法生成引用类
 */
public interface IApiPath {

    /**
     * 定义接口与实现类的构建关系
     */
    Map<String, String> getApiPathMap();

}

package com.sum.router.complier;

/**
 * Created by sdl on 2020/3/22
 */
public interface ProcessorConfig {

    //生成路由文件的包名
    String PACKAGE_NAME = "com.zp.apt.api.impl";

    //自定义生成类的接口路径
    String I_API_PATH = "com.zp.apt.api.IApiPath";
    //自动生成的文件名称
    String API_PATH_FILE_NAME = "ApiPathMapImpl";

    //Gradle传入的参
    //定义模块名称
    String MODULE_NAME = "moduleName";
}

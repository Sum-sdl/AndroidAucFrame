package com.sum.router.complier;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by sdl on 2019-05-22.
 */
abstract class BaseProcessor extends AbstractProcessor {
    //类，属性，函数都是element,操作element的工具类
    Elements elementUtils;

    //文件生成器,类,资源等
    Filer filer;
    //type 类信息的工具类
    Types typeUtils;

    //打印信息，Gradle中的日志
    Messager messager;

    //初始化方法，相当于Activity.OnCreate()方法
    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        elementUtils = env.getElementUtils();
        filer = env.getFiler();
        typeUtils = env.getTypeUtils();
        messager = env.getMessager();
        // Attempt to get user configuration [moduleName]
    }


    void print(Object o) {
        messager.printMessage(Diagnostic.Kind.NOTE, o.toString());
    }

    void printError(Object o) {
        messager.printMessage(Diagnostic.Kind.ERROR, o.toString());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        //支出的Annotation的类型，通过注解已经完成
        return super.getSupportedAnnotationTypes();
    }

    @Override
    public Set<String> getSupportedOptions() {
        //默认通过注解，设置App和APT的设置的值
        return super.getSupportedOptions();
    }

//以下是手动设置 注解和传参
//    @Override
//    public Set<String> getSupportedAnnotationTypes() {
//        //设置支持的注解
//        Set<String> set = new LinkedHashSet<>();
//        set.add(ApiRouter.class.getCanonicalName());
//        return set;
//    }
//
//    @Override
//    public Set<String> getSupportedOptions() {
//        //设置支持的入参
//        return new HashSet<String>() {{
//            this.add("MODULE_NAME");
//        }};
//    }

}

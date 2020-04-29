package com.sum.router.complier;

import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by sdl on 2020/3/22
 */
abstract class AbstractCreateClass {

    //类，属性，函数都是element,操作element的工具类
    Elements elementTool;

    //文件生成器,类,资源等
    Filer filer;
    //type 类信息的工具类
    Types typeTool;

    //打印信息，Gradle中的日志
    Messager messager;

    //初始化环境
    private ProcessingEnvironment mEnv;

    AbstractCreateClass(ProcessingEnvironment environment) {
        mEnv = environment;
        init(environment);
    }

    private void init(ProcessingEnvironment env) {
        elementTool = env.getElementUtils();
        filer = env.getFiler();
        typeTool = env.getTypeUtils();
        messager = env.getMessager();
    }

    //处理文件
    abstract boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv);

    void print(Object o) {
        messager.printMessage(Diagnostic.Kind.NOTE, o.toString());
    }

    void printError(Object o) {
        messager.printMessage(Diagnostic.Kind.ERROR, o.toString());
    }

}

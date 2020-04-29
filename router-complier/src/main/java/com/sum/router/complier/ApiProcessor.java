package com.sum.router.complier;

import com.google.auto.service.AutoService;

import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

/**
 * Created by sdl on 2019-05-22.
 */
//注册APT
@AutoService(Processor.class)
//处理的注解
@SupportedAnnotationTypes({"com.zhoupu.router.annotation.ApiList", "com.zhoupu.router.annotation.ApiImpl"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
//处理的自定义参数
@SupportedOptions({ProcessorConfig.MODULE_NAME})
public class ApiProcessor extends BaseProcessor {

    private AbstractCreateClass mClassCreate;

    //初始化
    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
//        Map<String, String> options = processingEnv.getOptions();
//        print("module->" + options.get(ProcessorConfig.MODULE_NAME));
        //构造方案生成类
        mClassCreate = new ApiFinderByString(env);
    }

    //开始
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //构造类
        return mClassCreate.process(annotations, roundEnv);
    }
}

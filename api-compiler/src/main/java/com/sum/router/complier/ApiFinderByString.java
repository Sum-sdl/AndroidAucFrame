package com.sum.router.complier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.zp.apt.annotation.ApiImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created by sdl on 2020/4/29
 * 构建ApiImpl的实现类
 */
class ApiFinderByString extends AbstractCreateClass {

    ApiFinderByString(ProcessingEnvironment environment) {
        super(environment);
    }

    //记录接口的全部实现类的路径
    private HashMap<String, String> allApiImpl = new HashMap<>();

    @Override
    boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//        print("process start1");
        //无注解直接返回
        if (annotations.isEmpty()) {
            return true;
        }
//        print("process start2");
        //获取所以接口的注解类
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ApiImpl.class);
        //构建Api的数据源
        //处理接口实现类
        for (Element element : elements) {
            //元素类型
            if (element.getKind() != ElementKind.CLASS) {
                print("元素不是类->" + element.toString());
                continue;
            }
            TypeElement typeElement = (TypeElement) element;
            //获取注解类似
            ApiImpl api = typeElement.getAnnotation(ApiImpl.class);
            //返回当前类的父类
//            TypeMirror superclass = typeElement.getSuperclass();
//            print("getSuperclass->" + superclass.toString());
            //该类实现的所有直接接口
            List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
            if (interfaces.size() != 1) {
                printError("ApiImpl注解的类必须有一个直接实现接口,并且只能实现一个");
                return false;
            }
            //实现的接口名称
            String implApi = interfaces.get(0).toString();
            //已经存在一个实现类了
            if (allApiImpl.containsKey(implApi)) {
                printError(implApi + " 已存在实现类:" + allApiImpl.get(implApi));
                return false;
            }
            //节点的全路径
            allApiImpl.put(implApi, typeElement.getQualifiedName().toString());
//            print("接口->" + implApi + " 实现类->" + allApiImpl.get(implApi));
        }
        //生成IApiPath接口Java实现类
        try {
            createApiPathFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    //构建java文件
    private void createApiPathFile() throws IOException {
        //无接口不用实现
        if (allApiImpl.isEmpty()) {
            return;
        }
        //生成方法

        //方法返回值 Map<String, String>
        ParameterizedTypeName methodReturn = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), ClassName.get(String.class));
        //生成方法
        //public Map<String, String> getApiPathMap()
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getApiPathMap")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(methodReturn);
        //方法生成行
        String apiMap = "apiMap";
        //HashMap<String, String> apiMap = new HashMap<>();
        methodBuilder.addStatement("$T<$T,$T> $N = new $T<>()",
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(String.class),
                apiMap,
                HashMap.class);
        // apiMap.put("aa", Object.class);
        for (Map.Entry<String, String> entry : allApiImpl.entrySet()) {
            methodBuilder.addStatement("$N.put($S,$S)", apiMap, entry.getKey(), entry.getValue());
        }
        //  return apiMap;
        methodBuilder.addStatement("return $N", apiMap);

        //生成类
        //获取接口类型
        TypeElement apiPathElement = elementTool.getTypeElement(ProcessorConfig.I_API_PATH);
        String finalClassName = mModuleName + "$" + ProcessorConfig.API_PATH_FILE_NAME;
        TypeSpec apiClass = TypeSpec.classBuilder(finalClassName)//类名
                .addSuperinterface(ClassName.get(apiPathElement))//实现的接口
                .addModifiers(Modifier.PUBLIC)//类是public修饰
                .addMethod(methodBuilder.build())//添加方法
                .build();
        //写入文件
        JavaFile.builder(ProcessorConfig.PACKAGE_NAME, apiClass).build().writeTo(filer);
    }


}




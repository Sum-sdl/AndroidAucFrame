package com.zhoupu.api

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension

/**
 * Created by sdl on 2020/4/30
 */
class ApiPlugin implements Plugin<Project> {


    static final String APT_MODUlE_NAME = "moduleName";

    @Override
    void apply(Project project) {
//        println("project name->" + project.name)
        //编译阶段
        //可以使用的工程类型
        if (!project.plugins.hasPlugin(AppPlugin)                                // AppPlugin
                && !project.plugins.hasPlugin(LibraryPlugin)                     // LibraryPlugin
        ) {
            throw new GradleException("android plugin required.")
        }
        //kotlin
        def isKotlinProject = project.plugins.hasPlugin('kotlin-android')
        if (isKotlinProject) {
            if (!project.plugins.hasPlugin('kotlin-kapt')) {
                project.plugins.apply('kotlin-kapt')
            }
        }

        //依赖方式
        String compileConf = 'implementation'
        String aptConf = 'annotationProcessor'
        if (isKotlinProject) {
            aptConf = 'kapt'
        }

        // Add dependencies
        Project routerProject = project.rootProject.findProject("api")
        Project compilerProject = project.rootProject.findProject("api-compiler")
        Project annotationProject = project.rootProject.findProject("api-annotation")
        if (routerProject && compilerProject && annotationProject) { // local
            project.dependencies.add(compileConf, routerProject)
            project.dependencies.add(compileConf, annotationProject)
            project.dependencies.add(aptConf, compilerProject)
        } else {
//            // org.gradle.api.internal.plugins.DefaultExtraPropertiesExtension
            ExtraPropertiesExtension ext = project.rootProject.ext
            //依赖的版本 在gradle.properties中配置
            String apiVersion = "1.1.1"
            String compilerVersion = "1.1.1"
            if (ext.has("apiVersion")) {
                apiVersion = ext.get("apiVersion")
            }
            if (ext.has("compilerVersion")) {
                compilerVersion = ext.get("compilerVersion")
            }
            project.dependencies.add(compileConf,
                    "com.github.Sum-sdl:api:${apiVersion}")
            project.dependencies.add(compileConf,
                    "com.github.Sum-sdl:api-annotation:${apiVersion}")
            project.dependencies.add(aptConf,
                    "com.github.Sum-sdl:api-compiler:${compilerVersion}")
        }
        //给android属性添加属性
        def android = project.extensions.findByName("android")
        if (android) {
            android.defaultConfig.javaCompileOptions.annotationProcessorOptions.argument(APT_MODUlE_NAME, project.name)
            android.productFlavors.all {
                it.javaCompileOptions.annotationProcessorOptions.argument(APT_MODUlE_NAME, project.name)
            }
        }
        //app工程添加
        if (project.plugins.hasPlugin(AppPlugin)) {
            def transform = new ApiTransform(project)
            android.registerTransform(transform)
        }
    }
}

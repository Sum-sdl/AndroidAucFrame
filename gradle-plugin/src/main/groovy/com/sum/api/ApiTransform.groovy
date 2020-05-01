package com.sum.api

import com.android.SdkConstants
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOCase
import org.apache.commons.io.filefilter.SuffixFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import org.gradle.api.Project

import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarFile

/**
 * Created by sdl on 2020/4/30
 */
class ApiTransform extends Transform {

    //文件包名
    private static final String API_FILE_PATCH = "com.zhoupu.api.processor.apt"

    //文件路径
    private static final String API_FILE_PATCH_DIR = API_FILE_PATCH.replace(".", File.separator)

    //忽略的jar包
    private static final Set<String> excludeJar = ["com.android.support", "android.arch.", "androidx."]

    Project project

    //class文件路径
    Set<String> initClasses

    ApiTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "ApiFinder"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        //设置对哪些类型进行transform进行转换
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        //设置插件使用的范围
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        //是否支持增量更新
        return false
    }

    //具体代码转换过程
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        printLog("--------------api transform start --------------")
        long startTime = System.currentTimeMillis()

        //记录生成的class路径
        initClasses = Collections.newSetFromMap(new ConcurrentHashMap<>())

        //输入，一种是文件夹，一种是jar(包含aar)
        def inputs = transformInvocation.inputs
        //文件输出
        def outputProvider = transformInvocation.outputProvider

        if (outputProvider != null) {
            //删除之前的文件
            outputProvider.deleteAll()
            //inputs 本地文件处理
            inputs.each {
                //遍历jar
                it.jarInputs.each {
                    handleJar(it, outputProvider)
                }
                //遍历本地Project的生成的class
                it.directoryInputs.each {
                    handleDirectory(it, outputProvider)
                }
            }
        }
        //找到的所有Api接口实现类
        initClasses.each {
            printLog(it)
        }

        printLog("--------------api transform finish -------------- time:" + (System.currentTimeMillis() - startTime))
    }

    void handleJar(JarInput jarInput, TransformOutputProvider outputProvider) throws IOException {
//        printLog("handleJar->" + jarInput.file.absolutePath + ",size:" + jarInput.file.size() + ",name:" + jarInput.name)
        //需要扫码的jar包
        if (shouldScanJar(jarInput)) {
            JarFile jarFile = new JarFile(jarInput.file)
            jarFile.entries().each {
                def fileName = it.name
                if (checkClass(fileName)) {
                    if (fileName.endsWith(SdkConstants.DOT_CLASS) && fileName.startsWith(API_FILE_PATCH_DIR)) {
                        String className = trimName(fileName, 0).replace(File.separator, '.')
                        initClasses.add(className)
                        printLog("handleJar->" + className)
                    }
                }
            }
        }
        //将任务抛给下一个transform
        File dsf = getJarDestFile(outputProvider, jarInput)
        FileUtils.copyFile(jarInput.file, dsf)
    }

    //过滤不需要扫码的jar文件
    static boolean shouldScanJar(JarInput jarInput) {
        excludeJar.each {
            if (jarInput.name.contains(it))
                return false
        }
        return true
    }

    //生成下文件的文件名
    static File getJarDestFile(TransformOutputProvider outputProvider, JarInput jarInput) {
        String destName = jarInput.name
        if (destName.endsWith(".jar")) { // local jar
            // rename to avoid the same name, such as classes.jar
            String hexName = DigestUtils.md5Hex(jarInput.file.absolutePath)
            destName = "${destName.substring(0, destName.length() - 4)}_${hexName}"
        }
        File destFile = outputProvider.getContentLocation(
                destName, jarInput.contentTypes, jarInput.scopes, Format.JAR)
        return destFile
    }


    //处理文件夹里面的class
    void handleDirectory(DirectoryInput directoryInput, TransformOutputProvider outputProvider) throws IOException {
//        printLog("handleDirectory->" + directoryInput.file.absolutePath + ",size:" + directoryInput.file.size())
        if (directoryInput.file.size() == 0) {
            return
        }
        //在目前文件中
        File packageDir = new File(directoryInput.file, API_FILE_PATCH_DIR)
        //遍历Api的文件夹
        if (packageDir.exists() && packageDir.isDirectory()) {
            //过滤文件
            def files = FileUtils.listFiles(packageDir, new SuffixFileFilter(SdkConstants.DOT_CLASS, IOCase.INSENSITIVE), TrueFileFilter.INSTANCE)
            files.each {
//                printLog(it.name + "," + it.absolutePath)
                String className = trimName(it.absolutePath, directoryInput.file.absolutePath.length() + 1).replace(File.separator, '.')
                printLog("handleDirectory-> " + className)
                initClasses.add(className);
            }
        }
        //获取 output 目录 dest：./app/build/intermediates/transforms/ApiFinder/
        def dsf = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
        //将input的目录复制到output目录
        FileUtils.copyDirectory(directoryInput.file, dsf)
    }

    //截取类路径
    static String trimName(String s, int start) {
        return s.substring(start, s.length() - SdkConstants.DOT_CLASS.length());
    }

    //一般的class
    static boolean checkClass(String fileName) {
        return fileName.endsWith(".class") && !fileName.startsWith("R\$") && "R.class" != fileName && "BuildConfig.class" != fileName
    }

    void printLog(String msg) {
        project.logger.error("ApiTransform->> " + msg)
    }

}

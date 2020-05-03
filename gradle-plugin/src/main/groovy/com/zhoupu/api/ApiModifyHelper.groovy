package com.zhoupu.api


import org.apache.commons.io.IOUtils
import org.objectweb.asm.*

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * Created by sdl on 2020/5/2
 */
class ApiModifyHelper {

    //ApiFinder Class类路径
    private static final String API_FINDER_CLASS = "com/zp/apt/api/ApiFinder"
    //需要修改的文件路径
    public static final String API_FINDER_CLASS_PATH = API_FINDER_CLASS + ".class"

    //新的文件路径
    private static Set<String> mApiClass

    //处理ApiFinder文件
    static void handle(Set<String> apiClass) {
        mApiClass = apiClass
        File targetFile = ApiTransform.apiFinderFile
        assert targetFile != null && targetFile.exists()
        if (targetFile.name.endsWith(".jar")) {
            //将需要修改的jar临时复制一份文件
            def optJar = new File(targetFile.getParent(), targetFile.name + ".opt")
            if (optJar.exists()) {
                optJar.delete()
            }
            def jarFile = new JarFile(targetFile)
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))
            Enumeration enumeration = jarFile.entries()
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = enumeration.nextElement()
                String entryName = jarEntry.name
                //构造新的临时文件
                ZipEntry zipEntry = new ZipEntry(entryName) // new entry
                jarOutputStream.putNextEntry(zipEntry)
                jarFile.getInputStream(jarEntry).withCloseable { is ->
                    //读取需要修改的文件
                    if (entryName == API_FINDER_CLASS_PATH) {
                        //修改后的jar文件
                        def bytes = modifyClass(is)
                        jarOutputStream.write(bytes)
                    } else {
                        jarOutputStream.write(IOUtils.toByteArray(is))
                    }
                    jarOutputStream.closeEntry()
                }
            }
            jarOutputStream.close()
            jarFile.close()
            //jar中需要修改文件
            targetFile.delete()
            optJar.renameTo(targetFile)
        } else if (targetFile.name.endsWith(".class")) { // 一般不会走到这里，因为AptHub位于jar包中
            modifyClass(new FileInputStream(targetFile))
        }
    }

    private static byte[] modifyClass(InputStream inputStream) {
        inputStream.withCloseable { is ->
            ClassReader cr = new ClassReader(is)
            ClassWriter cw = new ClassWriter(cr, 0)
            ClassVisitor cv = new AptClassVisitor(cw)
            cr.accept(cv, 0)
            return cw.toByteArray()
        }
    }

    /**
     * Delegate static code block
     */
    private static class AptClassVisitor extends ClassVisitor {
        AptClassVisitor(ClassVisitor cv) {
            super(Opcodes.ASM5, cv)
        }

        @Override
        MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)
            //处理静态方法
            if (name == "<clinit>") {
                mv = new ApiFinderInitMethod(mv)
            }
            return mv
        }
    }

    private static class ApiFinderInitMethod extends MethodVisitor {

        ApiFinderInitMethod(MethodVisitor methodVisitor) {
            super(Opcodes.ASM5, methodVisitor)
        }

        /**
         * Java code
         * <br>
         *     private static List<String> mAllApiClass = new ArrayList<>();
         *     static { <br>
         *         mAllApiClass.add("xxx.class");
         *         mAllApiClass.add("bbb.class");
         *}<br>
         *    ASM Code
         * <br>
         *  mv.visitLdcInsn("xxxx");
         *  mv.visitFieldInsn(GETSTATIC, "com/chenenyu/router/module/Hello", "mAllApiClass", "Ljava/util/ArrayList;");
         *  mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z", false);
         *  mv.visitInsn(POP);
         *  mv.visitFieldInsn(GETSTATIC, "com/chenenyu/router/module/Hello", "mAllApiClass", "Ljava/util/ArrayList;");
         *  mv.visitLdcInsn("xxxx");
         *  mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z", false);
         *  mv.visitInsn(POP);
         *  mv.visitFieldInsn(GETSTATIC, "com/chenenyu/router/module/Hello", "mAllApiClass", "Ljava/util/ArrayList;");
         *  mv.visitLdcInsn("xxxx");
         *  mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z", false);
         *  mv.visitInsn(POP);
         * <br>
         * */

        @Override
        void visitInsn(int opcode) {
            //在方法后插入代码
            if (opcode == Opcodes.RETURN) {
                mApiClass.each {
                    mv.visitFieldInsn(Opcodes.GETSTATIC, API_FINDER_CLASS, "mAllApiClass", "Ljava/util/ArrayList;")
                    mv.visitLdcInsn(it)
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z", false)
                    mv.visitInsn(Opcodes.POP)
                }
            }
            super.visitInsn(opcode)
        }
    }

}

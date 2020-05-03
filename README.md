
## Dependency
 > 插件版本 [ ![Download](https://api.bintray.com/packages/sum-sdl/android/api-gradle-plugin/images/download.svg) ](https://bintray.com/sum-sdl/android/api-gradle-plugin/_latestVersion)
   ```
    根目录
    build.gradle {
         dependencies {
                classpath 'com.android.tools.build:gradle:3.6.3'
                //Api实现依赖倒置框架
                classpath 'com.github.Sum-sdl:api-gradle-plugin:1.0.0'
            }
    }

    在需要使用的任意模块添加以下插件
    build.gradle {
        apply plugin: 'com.zhoupu.api'
    }
   ```

> **Api依赖倒置方案**
> 

- 通过APT生成单个模块的接口实现管理
- 通过ApiFinder类，来管理整个App的全部接口实现类
- 通过自定义插件，ASM动态插装代码，实现多模块的管理类合并

## App框架结构图
<div align="center">
<img src="https://github.com/Sum-sdl/AndroidAucFrame/raw/master/asset/Frame.png" height="845" width="890">
</div>
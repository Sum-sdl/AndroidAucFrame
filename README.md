
## Dependency
 > 引用插件 [ ![Download](https://api.bintray.com/packages/sum-sdl/android/api-gradle-plugin/images/download.svg) ](https://bintray.com/sum-sdl/android/api-gradle-plugin/_latestVersion)
   ```
    项目根目录
    build.gradle {
         dependencies {
                classpath 'com.android.tools.build:gradle:3.6.3'
                //Api实现依赖倒置框架
                classpath 'com.github.Sum-sdl:api-gradle-plugin:1.1.1'
            }
    }

    在需要使用Api的任意模块添加以下插件
    build.gradle {
        apply plugin: 'com.zhoupu.api'
    }
   ```

> **Api的使用方法**
```
//第一步
//定义模块对外提供功能的接口
public interface IApiFun {
    //定义的一个功能
    void toast();
}

//第二步
//在具体的类上增加 ApiImpl 注解，调用方是访问不到这个类的
@ApiImpl
class ApiFun implements IApiFun {
    @Override
    public void toast() {
        ToastUtils.showShort("ApiFun Impl do");
    }
}

//第三步
//在任意地方调用接口,此处是访问不了具体的实现类的
ApiFinder.get(IApiFun::class.java).toast()
```

> **核心解决的问题**
- **接口与实现类之间的依赖完全隔离**
- 所有模块之间的交互只依赖模块对外暴露的接口，不关注具体实现,调用方不依赖具体的实现类

> **Api依赖倒置方案**
- 通过APT生成单个模块的接口实现管理
- 通过ApiFinder类，管理整个App的全部接口实现类之间的关系
- 通过自定义插件，ASM动态插入代码，实现多模块的管理类合并


----

## App框架结构图
<div align="center">
<img src="https://github.com/Sum-sdl/AndroidAucFrame/raw/master/asset/Frame.png" height="845" width="890">
</div>
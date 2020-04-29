package com.zhoupu.router.api;

import java.lang.reflect.Constructor;
import java.util.HashMap;

/**
 * Created by sdl on 2020/4/29
 * 用于管理接口路径入口
 */
public class ApiFinder {

    //接口实现一一对应关系路径
    private static final String api_impl_class = "com.zhoupu.api.processor.apt.ApiPathMapImpl";

    private static ApiFinder router = new ApiFinder();

    //接口实现的集合类
    private HashMap<String, Object> apiImplMap = new HashMap<>();

    //接口和实现类对应关系
    private HashMap<String, String> mApiPath;

    private ApiFinder() {
    }

    private static ApiFinder get() {
        return router;
    }

    /**
     * @param api 接口类
     * @param <T> 返回的接口类型
     * @return 接口的实现类
     */
    public static <T> T findApi(Class<T> api) {
        String name = api.getName();
        HashMap<String, Object> apiImplMap = get().apiImplMap;
        //不存在实现类,初始化一次
        if (!apiImplMap.containsKey(name)) {
            Object o = get().newInstance(findClass(name));
            if (o != null) {
                apiImplMap.put(name, o);
                return (T) o;
            } else {
                return null;
            }
        } else {
            //返回缓存的接口
            return (T) apiImplMap.get(name);
        }
    }

    /**
     * @param name 任意字符串Key
     * @return 查找字符串对应的class
     */
    private static Class findClass(String name) {
        if (name == null || name.length() == 0) {
            return null;
        }
        //初始化接口类
        if (get().mApiPath == null) {
            get().init();
        }
        String className = get().mApiPath.get(name);
        if (className == null) {
            return null;
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    //初始化接口集合
    private void init() {
        Object api = null;
        try {
            api = newInstance(Class.forName(api_impl_class));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        mApiPath = new HashMap<>();
        //保存集合
        if (api != null) {
            IApiPath apiPath = (IApiPath) api;
            mApiPath.putAll(apiPath.getApiPathMap());
        }
    }

    //构建一个无参实例
    private Object newInstance(Class aClass) {
        try {
            if (aClass == null) {
                return null;
            }
            Constructor[] constructors = aClass.getDeclaredConstructors();
            Constructor constructor = constructors[0];
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

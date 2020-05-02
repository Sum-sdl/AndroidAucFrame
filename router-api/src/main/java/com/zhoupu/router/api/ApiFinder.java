package com.zhoupu.router.api;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sdl on 2020/4/29
 * 用于管理接口路径入口
 */
public class ApiFinder {

    private static ApiFinder router = new ApiFinder();

    //接口实现的缓存集合类
    private HashMap<String, Object> apiImplMap = new HashMap<>();

    //接口和实现类对应关系
    private HashMap<String, String> mApiPath;

    //多模块的路径,通过ASM动态插入的数据
    private static ArrayList<String> mAllApiClass = new ArrayList<>();

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
        //缓存的实现类
        HashMap<String, Object> apiImplMap = get().apiImplMap;
        //不存在实现类,初始化一次
        if (!apiImplMap.containsKey(name)) {
            Object o = get().newInstance(findClassByTag(name));
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

    //根据标记找到实现类
    private static String findClassByTag(String name) {
        if (name == null || name.length() == 0) {
            return null;
        }
        //初始化,初始化不能放在构造里面
        if (get().mApiPath == null) {
            get().init();
        }
        return get().mApiPath.get(name);
    }

    //构建一个无参实例
    private Object newInstance(String className) {
        try {
            if (className == null || className.length() == 0) {
                return null;
            }
            Class<?> aClass = Class.forName(className);
            Constructor[] constructors = aClass.getDeclaredConstructors();
            Constructor constructor = constructors[0];
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void init() {
        //初始化实现类路径
        if (!mAllApiClass.isEmpty()) {
            mApiPath = new HashMap<>();
            //保存实现类集合
            for (String apiClass : mAllApiClass) {
                Object api = newInstance(apiClass);
                if (api != null) {
                    IApiPath apiPath = (IApiPath) api;
                    mApiPath.putAll(apiPath.getApiPathMap());
                }
            }
        }
    }
}

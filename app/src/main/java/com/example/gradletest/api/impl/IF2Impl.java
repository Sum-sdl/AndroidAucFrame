package com.example.gradletest.api.impl;

import android.util.Log;

import com.example.gradletest.api.IApiFeature2;
import com.zp.apt.annotation.ApiImpl;

/**
 * Created by sdl on 2020/4/29
 */
@ApiImpl
class IF2Impl implements IApiFeature2 {
    @Override
    public void fun1() {
        Log.e("main", "IF2Impl fun1");
    }

    @Override
    public void fun2() {
        Log.e("main", "IF2Impl fun2");
    }
}

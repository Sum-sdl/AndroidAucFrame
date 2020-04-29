package com.example.gradletest.api.impl;

import android.util.Log;

import com.example.gradletest.api.IApiFeature1;
import com.zhoupu.router.annotation.ApiImpl;

/**
 * Created by sdl on 2020/4/29
 */
@ApiImpl
 class IF1Impl implements IApiFeature1 {
    @Override
    public void fun1() {
        Log.e("main", "IF1Impl fun1:" + this.toString());
    }
}

package com.example.goods;

import android.util.Log;

import com.zp.apt.annotation.ApiImpl;


/**
 * Created by sdl on 2020/4/28
 */
@ApiImpl
class browser implements IApiGoodFinder {
    @Override
    public void print() {
        Log.e("main", "browser print " + toString());
    }
}

package com.example.salebill.comment;

import android.util.Log;

import com.zhoupu.router.annotation.ApiImpl;

/**
 * Created by sdl on 2020/4/29
 */
@ApiImpl
class ModuleBill implements IModuleBill {
    @Override
    public void update() {
        Log.e("main", "ModuleBill update "+toString());
    }
}

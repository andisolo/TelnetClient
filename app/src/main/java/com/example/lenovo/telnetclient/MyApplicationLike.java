package com.example.lenovo.telnetclient;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * Created on 2017/11/30.
 *
 * @author GuoJiaming
 */

@DefaultLifeCycle(application = "com.example.lenovo.telnetclient.SampleApplication", flags =
        ShareConstants.TINKER_ENABLE_ALL)
public class MyApplicationLike extends DefaultApplicationLike {
    @SuppressLint("StaticFieldLeak")
    public MyApplicationLike(Application application, int tinkerFlags, boolean
            tinkerLoadVerifyFlag, long applicationStartElapsedTime, long
            applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime,
                applicationStartMillisTime, tinkerResultIntent);
    }

    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}

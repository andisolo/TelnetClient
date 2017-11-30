package com.example.lenovo.telnetclient;

import android.app.Application;
import android.content.Intent;

import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * Created on 2017/11/29.
 *
 * @author GuoJiaming
 */
@DefaultLifeCycle(application = "com.example.lenovo.telnetclient.MyApplication",
flags = ShareConstants.TINKER_ENABLE_ALL)
public class MyApplication extends DefaultApplicationLike {
    public MyApplication(Application application, int tinkerFlags, boolean
            tinkerLoadVerifyFlag, long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime,
                applicationStartMillisTime, tinkerResultIntent);
    }
}

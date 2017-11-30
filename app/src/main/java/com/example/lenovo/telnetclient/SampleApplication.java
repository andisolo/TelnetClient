package com.example.lenovo.telnetclient;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * Created on 2017/11/30.
 *
 * @author GuoJiaming
 */

public class SampleApplication extends TinkerApplication {
    protected SampleApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "tinker.sample.android.app.SampleApplicationLike");
    }
}

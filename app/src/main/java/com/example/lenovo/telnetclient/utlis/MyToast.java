package com.example.lenovo.telnetclient.utlis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import static android.widget.Toast.*;

/**
 * Created on 2017/11/15.
 *
 * @author GuoJiaming
 */

public class MyToast {
    private static Toast mToast;

    @SuppressLint("ShowToast")
    public static void showShort(Context context, String info) {
        if (mToast != null) {
            mToast.cancel();
            mToast = makeText(context, info, LENGTH_SHORT);
        } else {
            mToast = makeText(context, info, LENGTH_SHORT);
        }
        mToast.show();
    }
}

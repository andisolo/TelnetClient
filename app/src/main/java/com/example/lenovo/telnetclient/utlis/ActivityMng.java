package com.example.lenovo.telnetclient.utlis;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2017/10/30.
 *
 * @author GuoJiaming
 */

public class ActivityMng {
    private static List<Activity> list = new ArrayList<>();

    static void addActivity(Activity context) {
        list.add(context);

    }

    static void removeActivity(Activity context) {
        list.remove(context);
    }

    public static void removeAllActivity() {
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).isFinishing()) {
                list.get(i).finish();
            }
            list.get(i).finish();
        }
    }
}

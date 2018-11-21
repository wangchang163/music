package com.example.android.music.util;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.music.BuildConfig;

/**
 * 作者    wangchang
 * 时间    2018/10/30 15:37
 * 文件    music
 * 描述    日志工具類
 */
public class LogUtil {
    /**
     * 是否需要打印日志，可以在application的onCreate方法里面初始化
     * <p/>
     * 注意：调试的时候将isDebug设置为true，打印日志， app发布的时候将isDebug设置为false，不打印日志
     */
    public static boolean isDebug = BuildConfig.DEBUG;

    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (isDebug) {
            Log.v(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (isDebug) {
            Log.e(tag, msg, tr);
        }
    }

    public static void w(String tag, String msg) {
        if (isDebug) {
            Log.w(tag, msg);
        }
    }

    public static void println(int priority, String tag, String msg) {
        if (isDebug) {
            Log.println(priority, tag, msg);
        }
    }
}

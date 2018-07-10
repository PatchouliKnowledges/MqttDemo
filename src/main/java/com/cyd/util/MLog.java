package com.cyd.util;

import android.util.Log;

/**
 * Created by 斩断三千烦恼丝 on 2018/6/4.
 * used for Logcat控制台显示具体消息在哪一行，方便直接跳转
 */

public class MLog {
    public static boolean ifShown = true; //是否启用Log
    //   private static boolean ifSave = false;//是否保存Log
    private static String TAG = "MLog";


    public static void setTAG(String TAG) {
        MLog.TAG = TAG;
    }

    public static void setTAG(Object cls) {
        if (cls != null)
            MLog.TAG = cls.getClass().getSimpleName();
    }

    public static String getTAG() {
        return TAG;
    }

    public static void e(String msg) {
        if (!ifShown)
            return;
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.e(TAG, "(" + targetStackTraceElement.getFileName() + ":"
                + targetStackTraceElement.getLineNumber() + ") : "+msg);

    }


    public static void d(String msg) {
        if (!ifShown)
            return;
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.d(TAG, "(" + targetStackTraceElement.getFileName() + ":"
                + targetStackTraceElement.getLineNumber() + ") : "+msg);

    }


    private static StackTraceElement getTargetStackTraceElement() {
        StackTraceElement targetStackTrace = null;
        boolean shouldTrace = false;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            boolean isLogMethod = stackTraceElement.getClassName().equals(MLog.class.getName());
            if (shouldTrace && !isLogMethod) {
                targetStackTrace = stackTraceElement;
                break;
            }
            shouldTrace = isLogMethod;
        }
        return targetStackTrace;
    }
}

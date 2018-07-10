package com.cyd.mqttdemo;

import android.app.Application;

import com.cyd.util.MqttUtils;

/**
 * Created by 斩断三千烦恼丝 on 2018/7/10.
 */

public class MyApp extends Application {

    private static MyApp mp = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mp = this;
        //因为mqtt的生命周期基本是跟着整个应用的，所以建议放在application里面
        MqttUtils.getInstance().connect();
    }


    public static MyApp getApp() {
        return mp;
    }
}

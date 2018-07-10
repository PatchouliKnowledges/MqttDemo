package com.cyd.mqttdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cyd.bean.ServerMsg;
import com.cyd.util.MqttUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private TextView reciver;
    private EditText msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        initView();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().register(this);
    }

    private void initView() {
        reciver = findViewById(R.id.reciver);
        msg = findViewById(R.id.msg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showMsg(ServerMsg msg) {
        if (!TextUtils.isEmpty(msg.getMsg())) {
            reciver.append(msg.getMsg() + "\r\n");
        }
    }

    public void onClick(View v) {
        String msgText = msg.getText().toString();
        MqttUtils.getInstance().publishMessage(msgText);
    }


}

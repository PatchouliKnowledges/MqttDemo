package com.cyd.util;


import com.cyd.bean.ServerMsg;
import com.cyd.mqttdemo.MyApp;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;


/**
 * Created by 斩断三千烦恼丝 on 2018/6/4.
 */

public class MqttUtils {

    //**********初始化MQTT连接参数************
    private String serverUri = "tcp://192.168.0.112:1883"; //mqtt请求的服务器地址(记得改成你自己ip)
    private MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mqttConnectOptions;
    private final String subscriptionTopic = "Topic";//订阅的名字
  //  private final String topicConnectOrLost = "topicConnectOrLost"; //订阅的名字

    private String clientId = "cyd";


    public static MqttUtils getInstance() {
        return GetDefault.instance;
    }

    private static class GetDefault {
        static final MqttUtils instance = new MqttUtils();
    }

    private MqttUtils() {
    }


    /**
     * 调用默认的mqtt服务器地址进行连接
     */
    public void connect() {
        connect(serverUri);
    }


    /**
     * 指定mqtt服务器地址进行连接
     */
    public void connect(String url) {
        if (url == null || url.isEmpty()) {
            MLog.e("mqtt url 不能为空，或者空字符串");
            return;
        }
        if (!serverUri.equals(url))
            serverUri = url;
        mqttConnect();
    }

    /**
     * mqtt初始化连接操作
     */
    private void mqttConnect() {
        closeMQTT();
        mqttAndroidClient = new MqttAndroidClient(MyApp.getApp(), serverUri, clientId);

        mqttAndroidClient.setCallback(new MyCallBack());
        mqttConnectOptions = new MqttConnectOptions();
        //        mqttConnectOptions.setUserName();
        //        mqttConnectOptions.setPassword();
        mqttConnectOptions.setAutomaticReconnect(true);//自动重连
        mqttConnectOptions.setCleanSession(false); //不清除上一次的会话
    /*    mqttConnectOptions.setWill(topicConnectOrLost, (clientId + " close").getBytes(),
                2, true); //设置mqtt断连发送消息到后台*/

        connectMqtt(mqttConnectOptions);
    }

    /**
     * mqtt消息回调类
     */
    class MyCallBack implements MqttCallbackExtended {

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            if (reconnect) {
                MLog.e("Reconnected to : " + serverURI);
                subscribeToTopic();
            } else {
                MLog.e("Connected to: " + serverURI);
            }
        }

        @Override
        public void connectionLost(Throwable cause) {
            MLog.e("The Connection was lost.");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            final String str = new String(message.getPayload());
            MLog.e("Incoming message: " + str);
            EventBus.getDefault().post(new ServerMsg(str));
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    }


    /**
     * 连接activemq服务器
     */
    private void connectMqtt(MqttConnectOptions mqttConnectOptions) {
        try {

            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    MLog.e("mqtt连接成功");
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    MLog.e("mqtt连接失败, exception = " + exception.getMessage());
                }
            });

        } catch (MqttException ex) {
            MLog.e("connectMqtt: MqttException " + ex.getMessage());
        }
    }

    /**
     * 如果mqtt连接存在，则关闭mqtt连接
     */
    public void closeMQTT() {
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            try {
                mqttAndroidClient.disconnect();
                mqttAndroidClient.close();
                mqttAndroidClient = null;
                mqttConnectOptions = null;
            } catch (MqttException e) {
                MLog.e("mqtt关闭异常,MqttException = " + e.getMessage());
            }
        }
    }

    /**
     * 开始订阅主题
     */
    public void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    MLog.e("订阅成功");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    MLog.e("订阅失败");
                }
            });

        } catch (MqttException ex) {
            MLog.e("订阅发生异常..." + ex.getMessage());
        }
    }


    final String publishTopic = "ServerTopic";

    /**
     * 发送消息到activemq服务器
     */
    public void publishMessage(String msg) {

        try {

            MqttMessage message = new MqttMessage(msg.getBytes());
            mqttAndroidClient.publish(publishTopic, message);
            MLog.e("Message Published");

            if (!mqttAndroidClient.isConnected()) {
                MLog.e(mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
            }
        } catch (MqttException e) {
            MLog.e("Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }


}

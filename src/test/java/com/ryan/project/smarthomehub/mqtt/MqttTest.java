package com.ryan.project.smarthomehub.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * @Descritption
 * @Date 2020/12/31
 * @Author tangqianli
 */
public class MqttTest {
    public static void main(String[] args) {
        String subTopic = "testtopic/#";
        String pubTopic = "inTopic";
        String content = "Hello World";
        int qos = 2;
        String broker = "tcp://aliyun.ryantang.org:1883";
        String clientId = "emqx_test";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient client = new MqttClient(broker, clientId, persistence);

            // MQTT 连接选项
            MqttConnectOptions connOpts = new MqttConnectOptions();
//            connOpts.setUserName("emqx_test");
//            connOpts.setPassword("emqx_test_password".toCharArray());
            // 保留会话
            connOpts.setCleanSession(true);

            // 设置回调
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println("message arrived!");
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });

            // 建立连接
            System.out.println("Connecting to broker: " + broker);
            client.connect(connOpts);

            System.out.println("Connected");
            System.out.println("Publishing message: " + content);

            // 订阅
//            client.subscribe(subTopic);

            // 消息发布所需参数
            MqttMessage message = new MqttMessage(new byte[]{0,0});
            message.setQos(qos);
            client.publish(pubTopic, message);
            System.out.println("Message published");

            client.disconnect();
            System.out.println("Disconnected");
            client.close();
            System.exit(0);
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }
}

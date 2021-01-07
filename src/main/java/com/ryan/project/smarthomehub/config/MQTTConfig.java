package com.ryan.project.smarthomehub.config;

import com.ryan.project.smarthomehub.config.properties.MqttProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Descritption
 * @Date 2021/1/5
 * @Author tangqianli
 */
@Slf4j
@Configuration
public class MQTTConfig {

    @Autowired
    MqttProperties mqttProperties;

    /*@Bean
    MqttClient mqttClient() throws MqttException {

            MqttClient mqttClient = new MqttClient(mqttProperties.getBroker(), mqttProperties.getClientId(), new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            log.info("Connecting to broker: " + mqttProperties.getBroker());
            mqttClient.connect(connOpts);
            log.info("broker: "+mqttProperties.getBroker()+" connected");

            return mqttClient;
    }*/

    @Bean
    MqttAsyncClient mqttAsyncClient() throws MqttException {
        MqttAsyncClient mqttAsyncClient = new MqttAsyncClient(mqttProperties.getBroker(), mqttProperties.getClientId(), new MemoryPersistence());
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setAutomaticReconnect(true);

        log.info("Connecting to broker: " + mqttProperties.getBroker());

        mqttAsyncClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                log.info("broker: "+mqttProperties.getBroker()+" connected");
            }

            @Override
            public void connectionLost(Throwable cause) {
                log.error("broker: "+mqttProperties.getBroker()+" connection lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

            }

            @SneakyThrows
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                log.debug("topic: "+token.getTopics()+" content:"+new String(token.getMessage().getPayload())+" ...delivery completed");
            }
        });
        mqttAsyncClient.connect(connOpts);
        return mqttAsyncClient;
    }
}

package com.ryan.project.smarthomehub.module.fullfillment.service;

import com.ryan.project.smarthomehub.module.trait.DeviceStateReport;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeviceStateListener implements IMqttMessageListener {

    @Autowired
    ApplicationContext applicationContext;

    /**
     * topic format: state(fixed)/{user_id}/{device_model}/{device_id}
     * example: state/bob_id/zimi_heater_a2/12345
     *
     * @param topic
     * @param message
     * @throws Exception
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        log.info("receive state message: {}:{}", topic, message);
        String[] topicInfos = topic.split("/");
        String userId = topicInfos[1];
        String deviceModel = topicInfos[2];
        String deviceId = topicInfos[3];

        DeviceStateReport deviceStateReport = applicationContext.getBean(deviceModel, DeviceStateReport.class);
        deviceStateReport.reportDeviceState(userId, deviceId, message.toString());
    }
}

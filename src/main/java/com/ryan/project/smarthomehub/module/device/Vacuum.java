package com.ryan.project.smarthomehub.module.device;

import com.google.cloud.firestore.DocumentReference;
import com.ryan.project.smarthomehub.config.DeviceType;
import com.ryan.project.smarthomehub.module.trait.Dock;
import com.ryan.project.smarthomehub.module.trait.StartStop;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@DeviceType("action.devices.types.VACUUM")
@Service("dreamme_vacuum_w10_pro")
public class Vacuum extends Device implements StartStop, Dock {

    @Autowired
    MqttAsyncClient mqttAsyncClient;

    @Override
    public void processDock(DocumentReference documentReference, Map<String, Object> params) {
        documentReference.update("states.dock", true);
    }

    @Override
    public void processStartStop(DocumentReference documentReference, Map<String, Object> params) {

        try {
            Boolean start = (Boolean) params.get("start");
            //mqtt
            MqttMessage mqttMessage = new MqttMessage((start ? "start" : "stop").getBytes());
            mqttMessage.setQos(0);

            //send command to mqtt
            String topic = "vacuum/" + documentReference.getId();
            mqttAsyncClient.publish(topic, mqttMessage);

            if (start) {
                documentReference.update("states.isDocked", false);
                documentReference.update("states.isRunning", true);
            } else {
                documentReference.update("states.isRunning", false);
                documentReference.update("states.isDocked", true);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }
}

package com.ryan.project.smarthomehub.module.device;

import com.google.cloud.firestore.DocumentReference;
import com.ryan.project.smarthomehub.module.trait.OnOff;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @Descritption
 * @Date 2020/12/29
 * @Author tangqianli
 */
@Slf4j
@Service("action.devices.types.LIGHT")
public class Light extends Device implements OnOff {

    @Autowired
    MqttAsyncClient mqttAsyncClient;

    @Override
    public void processOnOff(DocumentReference documentReference, Map<String, Object> params) {

        log.debug("process light OnOff command,params:{}",params);
        boolean on = (boolean) params.get("on");

        //mqtt
        MqttMessage mqttMessage = new MqttMessage("hello world".getBytes(StandardCharsets.UTF_8));
        mqttMessage.setQos(0);

        try {
            //send command to mqtt
            String topic = "device/" + documentReference.getId();
            mqttAsyncClient.publish(topic,mqttMessage);

            //update firestore date
            documentReference.update("states.on", on);
        } catch (MqttException e) {
            log.error("", e);
        }

    }
}

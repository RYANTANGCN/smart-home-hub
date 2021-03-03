package com.ryan.project.smarthomehub.module.device;

import com.google.cloud.firestore.DocumentReference;
import com.ryan.project.smarthomehub.config.DeviceType;
import com.ryan.project.smarthomehub.module.trait.OnOff;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Descritption
 * @Date 2020/12/29
 * @Author tangqianli
 */
@Slf4j
@DeviceType("action.devices.types.LIGHT")
@Service("virtual_led_light")
public class Light extends Device implements OnOff {

    @Autowired
    MqttAsyncClient mqttAsyncClient;

    @Override
    public void processOnOff(DocumentReference documentReference, Map<String, Object> params) {

        log.debug("process light OnOff command,params:{}", params);
        boolean on = (boolean) params.get("on");
        byte b = on?(byte)1:(byte)0;

        try {
            //mqtt
//            ObjectMapper objectMapper = new ObjectMapper();
            MqttMessage mqttMessage = new MqttMessage(new byte[]{0,b});
            mqttMessage.setQos(0);

            //send command to mqtt
            String topic = "light/" + documentReference.getId();
            mqttAsyncClient.publish(topic, mqttMessage);

            //update firestore date
            documentReference.update("states.on", on);
        } catch (Exception e) {
            log.error("", e);
        }

    }
}

package com.ryan.project.smarthomehub.module.device;

import com.google.cloud.firestore.DocumentReference;
import com.ryan.project.smarthomehub.module.trait.Dispense;
import com.ryan.project.smarthomehub.module.trait.OnOff;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Descritption
 * @Date 2021/1/14
 * @Author tangqianli
 */
@Slf4j
@Service("action.devices.types.PETFEEDER")
public class PetFeeder extends Device implements OnOff, Dispense {

    @Autowired
    MqttAsyncClient mqttAsyncClient;

    @Override
    public void processOnOff(DocumentReference documentReference, Map<String, Object> params) {
        log.debug("process pet feeder OnOff command,params:{}", params);

        boolean on = (boolean) params.get("on");
        byte b = on ? (byte) 1 : (byte) 0;

        try {
            //mqtt
            MqttMessage mqttMessage = new MqttMessage(new byte[]{0, b});
            mqttMessage.setQos(0);

            //send command to mqtt
            String topic = "petfeeder/" + documentReference.getId();
            mqttAsyncClient.publish(topic, mqttMessage);

            //update firestore date
            documentReference.update("states.on", on);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void processDispense(DocumentReference documentReference, Map<String, Object> params) {

    }
}

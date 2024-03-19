package com.ryan.project.smarthomehub.module.device;

import com.google.cloud.firestore.DocumentReference;
import com.google.gson.Gson;
import com.ryan.project.smarthomehub.config.DeviceType;
import com.ryan.project.smarthomehub.module.trait.Brightness;
import com.ryan.project.smarthomehub.module.trait.OnOff;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Descritption
 * @Date 2020/12/29
 * @Author tangqianli
 */
@Slf4j
@DeviceType("action.devices.types.LIGHT")
@Service("yeelink_light_lamp1")
public class Light extends Device implements OnOff , Brightness,BeanNameAware {

    @Autowired
    MqttAsyncClient mqttAsyncClient;

    private String beanName;

    private Gson gson = new Gson();

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void processOnOff(DocumentReference documentReference, Map<String, Object> params) {

        log.debug("process light OnOff command,params:{}", params);
        boolean on = (boolean) params.get("on");
        String deviceId = documentReference.getId();
        String userId = documentReference.getParent().getParent().getId();
        Map<String, Object> map = new HashMap<>();
        map.put("on", on);

        try {
            //mqtt
            MqttMessage mqttMessage = new MqttMessage(gson.toJson(map).getBytes());
            mqttMessage.setQos(0);

            //send command to mqtt
           String topic = String.format("light/%s/%s/%s", userId, beanName, deviceId);
            mqttAsyncClient.publish(topic, mqttMessage);

            //update firestore date
            documentReference.update("states.on", on);
        } catch (Exception e) {
            log.error("", e);
        }

    }

    @Override
    public void processBrightnessAbsolute(DocumentReference documentReference, Map<String, Object> params) {
        log.debug("process light OnOff command,params:{}", params);
        Integer brightness = (Integer) params.get("brightness");
        String deviceId = documentReference.getId();
        String userId = documentReference.getParent().getParent().getId();
        Map<String, Object> map = new HashMap<>();
        map.put("brightness", brightness);

        try {
            //mqtt
            MqttMessage mqttMessage = new MqttMessage(gson.toJson(map).getBytes());
            mqttMessage.setQos(0);

            //send command to mqtt
            String topic = String.format("light/%s/%s/%s", userId, beanName, deviceId);
            mqttAsyncClient.publish(topic, mqttMessage);

            //update firestore date
            documentReference.update("states.brightness", brightness);
        } catch (Exception e) {
            log.error("", e);
        }
    }
}

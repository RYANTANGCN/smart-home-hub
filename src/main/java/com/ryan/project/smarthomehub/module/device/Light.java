package com.ryan.project.smarthomehub.module.device;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ryan.project.smarthomehub.config.DeviceType;
import com.ryan.project.smarthomehub.module.event.ReportStateEvent;
import com.ryan.project.smarthomehub.module.trait.Brightness;
import com.ryan.project.smarthomehub.module.trait.DeviceStateReport;
import com.ryan.project.smarthomehub.module.trait.OnOff;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
public class Light extends Device implements OnOff, Brightness, DeviceStateReport, BeanNameAware {

    @Autowired
    MqttAsyncClient mqttAsyncClient;

    @Autowired
    Firestore database;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

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
        //set brightness instead of turning on to keep the same brightness last time
        if (on) {
            try {
                Integer brightness = (Integer) documentReference.get().get().get("states.brightness");
                map.put("brightness", brightness);
            } catch (Exception e) {
                log.error("get brightness state error", e);
            }
        } else {
            map.put("on", on);
        }

        try {
            //mqtt
            MqttMessage mqttMessage = new MqttMessage(gson.toJson(map).getBytes());
            mqttMessage.setQos(0);

            //send command to mqtt
            String topic = String.format("light/%s/%s/%s", userId, beanName, deviceId);
            mqttAsyncClient.publish(topic, mqttMessage);

            //update firestore date
            documentReference.update("states.on", on);

            //report state
            DocumentSnapshot documentSnapshot = documentReference.get().get();
            Map<String, Object> states = (Map<String, Object>) documentSnapshot.get("states");
            states.put("on", on);
            Map<String, Object> updateStates = new HashMap<>();
            updateStates.put(deviceId, states);
            applicationEventPublisher.publishEvent(new ReportStateEvent(this, userId, updateStates));
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

            //report state
            DocumentSnapshot documentSnapshot = documentReference.get().get();
            Map<String, Object> states = (Map<String, Object>) documentSnapshot.get("states");
            states.put("brightness", brightness);
            Map<String, Object> updateStates = new HashMap<>();
            updateStates.put(deviceId, states);
            applicationEventPublisher.publishEvent(new ReportStateEvent(this, userId, updateStates));
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void reportDeviceState(String userId, String deviceId, String message) {
        DocumentReference documentReference = database
                .collection("users")
                .document(userId)
                .collection("devices").document(deviceId);
        Assert.notNull(documentReference, "device should not be null with deviceId:" + deviceId);

        try {
            JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
            Boolean on = jsonObject.get("on").getAsBoolean();
            Integer brightness = jsonObject.get("brightness").getAsInt();

            //update database
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("states.on", on);
            updateMap.put("states.brightness", brightness);
            documentReference.update(updateMap);

            //report state
            DocumentSnapshot documentSnapshot = documentReference.get().get();
            Map<String, Object> states = (Map<String, Object>) documentSnapshot.get("states");
            states.put("on", on);
            states.put("brightness", brightness);
            Map<String, Object> updateStates = new HashMap<>();
            updateStates.put(deviceId, states);
            applicationEventPublisher.publishEvent(new ReportStateEvent(this, userId, updateStates));
        } catch (Exception e) {

        }
    }
}

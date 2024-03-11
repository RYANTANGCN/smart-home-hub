package com.ryan.project.smarthomehub.module.device;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ryan.project.smarthomehub.config.DeviceType;
import com.ryan.project.smarthomehub.module.trait.DeviceStateReport;
import com.ryan.project.smarthomehub.module.trait.OnOff;
import com.ryan.project.smarthomehub.module.trait.TemperatureSetting;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@DeviceType("action.devices.types.HEATER")
@Service("smart_heater")
public class Heater extends Device implements TemperatureSetting, OnOff, DeviceStateReport {

    @Autowired
    MqttAsyncClient mqttAsyncClient;

    @Autowired
    Firestore database;

    private Gson gson = new Gson();

    @Override
    public void processTemperatureSetPoint(DocumentReference documentReference, Map<String, Object> params) {
        log.info("received command:{}", params);
        Integer degrees = (Integer) params.get("thermostatTemperatureSetpoint");
        try {
            //mqtt
            Map<String, Object> mqttMsg = new HashMap<>();
            mqttMsg.put("setpoint", degrees);
            MqttMessage mqttMessage = new MqttMessage(gson.toJson(mqttMsg).toString().getBytes());
            mqttMessage.setQos(0);

            //send command to mqtt
            //TODO should add user ID to the topic
            String topic = "heater/" + documentReference.getId();
            mqttAsyncClient.publish(topic, mqttMessage);

            //update firestore date
            documentReference.update("states.thermostatTemperatureSetpoint", degrees);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void processOnOff(DocumentReference documentReference, Map<String, Object> params) {
        log.info("received command:{}", params);
        boolean on = (Boolean) params.get("on");
        try {
            //mqtt
            Map<String, Object> mqttMsg = new HashMap<>();
            mqttMsg.put("on", on);
            MqttMessage mqttMessage = new MqttMessage(gson.toJson(mqttMsg).toString().getBytes());
            mqttMessage.setQos(0);

            //send command to mqtt
            String topic = "heater/" + documentReference.getId();
            mqttAsyncClient.publish(topic, mqttMessage);

            //update firestore date
            documentReference.update("states.on", on);
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
            JsonElement jsonElement = JsonParser.parseString(message);
            BigDecimal environmentTemperature = jsonElement.getAsJsonObject().get("environment_temperature").getAsBigDecimal();
            String oldTemperature = documentReference.get().get().get("states.thermostatTemperatureAmbient", String.class);
            if (!Integer.valueOf(oldTemperature).equals(environmentTemperature.intValue())) {
                documentReference.update("states.thermostatTemperatureAmbient", environmentTemperature);
            }
        } catch (Exception e) {
            log.error("error update environment temperature", e);
        }
    }
}
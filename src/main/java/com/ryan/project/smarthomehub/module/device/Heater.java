package com.ryan.project.smarthomehub.module.device;

import com.google.api.services.homegraph.v1.HomeGraphService;
import com.google.api.services.homegraph.v1.model.ReportStateAndNotificationDevice;
import com.google.api.services.homegraph.v1.model.ReportStateAndNotificationRequest;
import com.google.api.services.homegraph.v1.model.StateAndNotificationPayload;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ryan.project.smarthomehub.config.DeviceType;
import com.ryan.project.smarthomehub.module.event.ReportStateEvent;
import com.ryan.project.smarthomehub.module.trait.DeviceStateReport;
import com.ryan.project.smarthomehub.module.trait.OnOff;
import com.ryan.project.smarthomehub.module.trait.TemperatureSetting;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@DeviceType("action.devices.types.HEATER")
@Service("smart_heater")
public class Heater extends Device implements TemperatureSetting, OnOff, DeviceStateReport, BeanNameAware {


    @Autowired
    MqttAsyncClient mqttAsyncClient;

    @Autowired
    Firestore database;

    @Autowired
    HomeGraphService homeGraphService;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    private String beanName;

    private Gson gson = new Gson();

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void processTemperatureSetPoint(DocumentReference documentReference, Map<String, Object> params) {
        log.info("received command:{}", params);

        String deviceId = documentReference.getId();
        String userId = documentReference.getParent().getParent().getId();
        Integer degrees = (Integer) params.get("thermostatTemperatureSetpoint");
        try {
            //mqtt
            Map<String, Object> mqttMsg = new HashMap<>();
            mqttMsg.put("setpoint", degrees);
            MqttMessage mqttMessage = new MqttMessage(gson.toJson(mqttMsg).toString().getBytes());
            mqttMessage.setQos(0);

            //send command to mqtt
            String topic = String.format("heater/%s/%s/%s", userId, beanName, documentReference.getId());
            mqttAsyncClient.publish(topic, mqttMessage);

            //update firestore date
            documentReference.update("states.thermostatTemperatureSetpoint", degrees);

            // Report device state.
            Map<String, Object> reportMap = (Map<String, Object>) documentReference.get().get().get("states");
            reportMap.put("thermostatTemperatureSetpoint", degrees);
            applicationEventPublisher.publishEvent(new ReportStateEvent(this, userId, new HashMap() {{
                put(deviceId, reportMap);
            }}));
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void processThermostatSetMode(DocumentReference documentReference, Map<String, Object> params) {
        log.info("received command:{}", params);
        String deviceId = documentReference.getId();
        String thermostatMode = (String) params.get("thermostatMode");
        String userId = documentReference.getParent().getParent().getId();
        try {
            //mqtt
            Map<String, Object> mqttMsg = new HashMap<>();
            mqttMsg.put("on", "auto".equals(thermostatMode) ? true : false);
            MqttMessage mqttMessage = new MqttMessage(gson.toJson(mqttMsg).toString().getBytes());
            mqttMessage.setQos(0);

            //send command to mqtt
            String topic = String.format("heater/%s/%s/%s", userId, beanName, documentReference.getId());
            mqttAsyncClient.publish(topic, mqttMessage);

            //update firestore date
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("states.activeThermostatMode", thermostatMode);
            updateMap.put("states.thermostatMode", thermostatMode);
            updateMap.put("on", "auto".equals(thermostatMode) ? true : false);
            documentReference.update(updateMap);

            // Report device state.
            Map<String, Object> reportMap = (Map<String, Object>) documentReference.get().get().get("states");
            reportMap.put("activeThermostatMode", thermostatMode);
            reportMap.put("thermostatMode", thermostatMode);
            reportMap.put("on", "auto".equals(thermostatMode) ? true : false);
            applicationEventPublisher.publishEvent(new ReportStateEvent(this, userId, new HashMap() {{
                put(deviceId, reportMap);
            }}));
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void processOnOff(DocumentReference documentReference, Map<String, Object> params) {
        log.info("received command:{}", params);
        String deviceId = documentReference.getId();
        boolean on = (Boolean) params.get("on");
        String userId = documentReference.getParent().getParent().getId();
        try {
            //mqtt
            Map<String, Object> mqttMsg = new HashMap<>();
            mqttMsg.put("on", on);
            MqttMessage mqttMessage = new MqttMessage(gson.toJson(mqttMsg).toString().getBytes());
            mqttMessage.setQos(0);

            //send command to mqtt
            String topic = String.format("heater/%s/%s/%s", userId, beanName, documentReference.getId());
            mqttAsyncClient.publish(topic, mqttMessage);

            //update firestore date
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("states.activeThermostatMode", on ? "auto" : "off");
            updateMap.put("states.thermostatMode", on ? "auto" : "off");
            updateMap.put("states.on", on);
            documentReference.update(updateMap);

            // Report device state.
            Map<String, Object> reportMap = (Map<String, Object>) documentReference.get().get().get("states");
            reportMap.put("activeThermostatMode", on ? "auto" : "off");
            reportMap.put("thermostatMode", on ? "auto" : "off");
            reportMap.put("on", on);
            applicationEventPublisher.publishEvent(new ReportStateEvent(this, userId, new HashMap() {{
                put(deviceId, reportMap);
            }}));
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
            BigDecimal thermostatTemperatureAmbient = jsonObject.get("thermostatTemperatureAmbient").getAsBigDecimal();
            BigDecimal thermostatTemperatureSetpoint = jsonObject.get("thermostatTemperatureSetpoint").getAsBigDecimal();
            Boolean onState = jsonObject.get("on").getAsBoolean();
            DocumentSnapshot documentSnapshot = documentReference.get().get();

            //get last states' value
            Long lastThermostatTemperatureAmbient = documentSnapshot.get("states.thermostatTemperatureAmbient", Long.class);
            Long lastThermostatTemperatureSetpoint = documentSnapshot.get("states.thermostatTemperatureSetpoint", Long.class);
            Boolean lastOnState = documentSnapshot.get("states.on", Boolean.class);


            Map<String, Object> states = (Map<String, Object>) documentSnapshot.get("states");

            Map<String, Object> updateMap = new HashMap<>();
            if (lastThermostatTemperatureAmbient != (thermostatTemperatureAmbient.longValue())) {
                updateMap.put("states.thermostatTemperatureAmbient", thermostatTemperatureAmbient.longValue());
                states.put("thermostatTemperatureAmbient", thermostatTemperatureAmbient.longValue());
            }
            if (lastThermostatTemperatureSetpoint != thermostatTemperatureSetpoint.longValue()) {
                updateMap.put("states.thermostatTemperatureSetpoint", thermostatTemperatureSetpoint.longValue());
                states.put("thermostatTemperatureSetpoint", thermostatTemperatureSetpoint.longValue());
            }
            if (!onState.equals(lastOnState)) {
                updateMap.put("states.on", onState);
                states.put("on", onState);
            }
            if (!updateMap.isEmpty()) {
                //update firestore database
                documentReference.update(updateMap);
                log.info("{} update states:{}", beanName, updateMap);

                // Report device state.
                applicationEventPublisher.publishEvent(new ReportStateEvent(this, userId, new HashMap() {{
                    put(deviceId, states);
                }}));
            }
        } catch (Exception e) {
            log.error("error update environment temperature", e);
        }
    }
}

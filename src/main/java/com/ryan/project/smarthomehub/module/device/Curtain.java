package com.ryan.project.smarthomehub.module.device;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.DocumentReference;
import com.ryan.project.smarthomehub.config.DeviceType;
import com.ryan.project.smarthomehub.module.trait.OpenClose;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Descritption
 * @Date 2020/12/25
 * @Author tangqianli
 */
@Slf4j
@DeviceType("action.devices.types.CURTAIN")
@Service("dooya_curtain_m1")
public class Curtain extends Device implements OpenClose {

    @Autowired
    MqttAsyncClient mqttAsyncClient;

    @Override
    public void processOpenClose(DocumentReference documentReference, Map<String, Object> params) {
        log.debug("process curtain OpenClose traits,params:{}", params);
        int openPercent = (int) params.get("openPercent");
        List<Map<String, Object>> openState = new ArrayList<>();
        openState.add(new HashMap<>(){{put("openDirection","LEFT");put("openPercent",openPercent);}});
        openState.add(new HashMap<>(){{put("openDirection","RIGHT");put("openPercent",openPercent);}});

        try {
            //send command to mqtt
            ObjectMapper objectMapper = new ObjectMapper();
            //Doesn't support open or close one side individually
            Map<String, Object> messageMap = new HashMap<>() {{
                put("openPercent", openPercent);
                put("customData", documentReference.get().get().get("customData"));
            }};
            MqttMessage mqttMessage = new MqttMessage(objectMapper.writeValueAsBytes(messageMap));
            mqttMessage.setQos(0);
            String topic = "curtain/" + documentReference.getId();
            mqttAsyncClient.publish(topic, mqttMessage);

            //update firestore data
            documentReference.update("states.openState", openState);

        } catch (Exception e) {
            log.error("", e);
        }

    }
}

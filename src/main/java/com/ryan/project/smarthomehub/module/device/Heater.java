package com.ryan.project.smarthomehub.module.device;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.ryan.project.smarthomehub.module.trait.TemperatureSetting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Map;

@Slf4j
@Component
public class Heater extends Device implements TemperatureSetting {
    @Override
    public Map<String, Object> processQuery(DocumentSnapshot device, Map<String, Object> customData) {
        return super.processQuery(device, customData);
    }

    @Override
    public void processTemperatureSetting(DocumentReference documentSnapshot, Map<String, Object> params) {
        log.info("received command:{}", params);
    }
}

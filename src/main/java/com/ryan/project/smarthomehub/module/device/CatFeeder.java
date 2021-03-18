package com.ryan.project.smarthomehub.module.device;

import com.google.cloud.firestore.DocumentReference;
import com.ryan.project.smarthomehub.config.DeviceType;
import com.ryan.project.smarthomehub.module.trait.Dispense;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@DeviceType("action.devices.types.PETFEEDER")
@Service("esp_cat_food_feeder")
public class CatFeeder extends Device implements Dispense {

    @Override
    public void processDispense(DocumentReference documentReference, Map<String, Object> params) {

    }
}

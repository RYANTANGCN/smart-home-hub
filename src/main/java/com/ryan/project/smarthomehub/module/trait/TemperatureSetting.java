package com.ryan.project.smarthomehub.module.trait;

import com.google.cloud.firestore.DocumentReference;
import com.ryan.project.smarthomehub.config.Command;

import java.util.Map;

public interface TemperatureSetting {

    @Command("action.devices.traits.TemperatureSetting")
    void processTemperatureSetting(DocumentReference documentSnapshot, Map<String, Object> params);
}

package com.ryan.project.smarthomehub.module.trait;

import com.google.cloud.firestore.DocumentReference;
import com.ryan.project.smarthomehub.config.Command;

import java.util.Map;

public interface Brightness {

    @Command("action.devices.commands.BrightnessAbsolute")
    void processBrightnessAbsolute(DocumentReference documentReference, Map<String, Object> params);

    @Command("action.devices.commands.BrightnessRelative")
    default void processBrightnessRelative(DocumentReference documentReference, Map<String, Object> params) {
    }
}

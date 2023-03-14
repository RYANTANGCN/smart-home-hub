package com.ryan.project.smarthomehub.module.trait;

import com.google.cloud.firestore.DocumentReference;
import com.ryan.project.smarthomehub.config.Command;

import java.util.Map;

public interface StartStop {

    @Command("action.devices.commands.StartStop")
    void processStartStop(DocumentReference documentReference, Map<String, Object> params);
}

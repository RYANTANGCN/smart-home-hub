package com.ryan.project.smarthomehub.module.trait;

import com.google.cloud.firestore.DocumentReference;
import com.ryan.project.smarthomehub.config.Command;

import java.util.Map;

public interface Dock {

    @Command("action.devices.commands.Dock")
    void processDock(DocumentReference documentReference, Map<String, Object> params);
}

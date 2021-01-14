package com.ryan.project.smarthomehub.module.trait;

import com.google.cloud.firestore.DocumentReference;
import com.ryan.project.smarthomehub.config.Command;

import java.util.Map;

/**
 * @Descritption
 * @Date 2021/1/14
 * @Author tangqianli
 */
public interface Dispense {

    @Command("action.devices.commands.Dispense")
    void processDispense(DocumentReference documentReference, Map<String, Object> params);
}

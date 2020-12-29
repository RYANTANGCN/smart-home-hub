package com.ryan.project.smarthomehub.module.trait;

import com.google.cloud.firestore.DocumentReference;
import com.ryan.project.smarthomehub.config.Command;

import java.util.Map;

/**
 * @Descritption
 * @Date 2020/12/25
 * @Author tangqianli
 */
public interface OpenClose{
    @Command("action.devices.commands.OpenClose")
    void processOpenClose(DocumentReference documentSnapshot, Map<String, Object> params);
}

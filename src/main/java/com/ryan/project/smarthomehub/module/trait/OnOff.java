package com.ryan.project.smarthomehub.module.trait;

import com.google.cloud.firestore.DocumentSnapshot;
import com.ryan.project.smarthomehub.config.Command;

import java.util.Map;

/**
 * @Descritption
 * @Date 2020/12/29
 * @Author tangqianli
 */
public interface OnOff {
    @Command("action.devices.commands.OnOff")
    void processOnOff(DocumentSnapshot documentSnapshot, Map<String, Object> params);
}

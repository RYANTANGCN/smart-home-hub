package com.ryan.project.smarthomehub.module.device;

import com.google.actions.api.smarthome.ExecuteRequest;
import com.google.cloud.firestore.DocumentSnapshot;

/**
 * @Descritption
 * @Date 2020/12/25
 * @Author tangqianli
 */
public interface IDevice {

    String processTraits(DocumentSnapshot device, ExecuteRequest.Inputs.Payload.Commands.Execution[] executions);
}

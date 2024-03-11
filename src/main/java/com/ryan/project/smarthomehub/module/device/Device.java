package com.ryan.project.smarthomehub.module.device;

import com.google.actions.api.smarthome.ExecuteRequest;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.ryan.project.smarthomehub.config.Command;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Descritption
 * @Date 2020/12/25
 * @Author tangqianli
 */
public class Device {

    public Map<String, Object> processQuery(DocumentSnapshot device, Map<String, Object> customData) {

        Map<String, Object> deviceState = (Map<String, Object>) device.get("states");

        if (deviceState == null) {
            deviceState = new HashMap<>();
        }

        deviceState.put("online", true);
        deviceState.put("status", "SUCCESS");
        return deviceState;
    }

    @SneakyThrows
    public String processTraits(DocumentReference device, ExecuteRequest.Inputs.Payload.Commands.Execution execution) {
        for (Class<?> anInterface : this.getClass().getInterfaces()) {
            for (Method method : anInterface.getMethods()) {
                if (method.isAnnotationPresent(Command.class)) {
                    Command command = method.getAnnotation(Command.class);

                    if (execution.command.equals(command.value())) {
                        method.invoke(this, device, execution.getParams());
                    }
                }
            }
        }
        return device.getId();
    }
}

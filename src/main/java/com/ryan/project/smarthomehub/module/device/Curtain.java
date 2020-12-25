package com.ryan.project.smarthomehub.module.device;

import com.google.actions.api.smarthome.ExecuteRequest;
import com.google.cloud.firestore.DocumentSnapshot;
import com.ryan.project.smarthomehub.module.trait.IExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * @Descritption
 * @Date 2020/12/25
 * @Author tangqianli
 */
@Service("action.devices.types.CURTAIN")
public class Curtain implements IDevice{

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public String processTraits(DocumentSnapshot device, ExecuteRequest.Inputs.Payload.Commands.Execution[] executions) {
        for (ExecuteRequest.Inputs.Payload.Commands.Execution execution : executions) {
            IExecution concreteExecution = (IExecution) applicationContext.getBean(execution.command);
            concreteExecution.processExecution(device, execution.getParams());
        }
        return null;
    }
}

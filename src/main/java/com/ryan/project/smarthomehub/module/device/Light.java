package com.ryan.project.smarthomehub.module.device;

import com.google.cloud.firestore.DocumentReference;
import com.ryan.project.smarthomehub.module.trait.OnOff;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Descritption
 * @Date 2020/12/29
 * @Author tangqianli
 */
@Slf4j
@Service("action.devices.types.LIGHT")
public class Light extends Device implements OnOff {
    @Override
    public void processOnOff(DocumentReference documentReference, Map<String, Object> params) {

        log.debug("process light OnOff command,params:{}",params);
        boolean on = (boolean) params.get("on");
        documentReference.update("states.on", on);
    }
}

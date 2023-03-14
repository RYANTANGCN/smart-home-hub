package com.ryan.project.smarthomehub.module.device;

import com.google.cloud.firestore.DocumentReference;
import com.ryan.project.smarthomehub.config.DeviceType;
import com.ryan.project.smarthomehub.module.trait.Dock;
import com.ryan.project.smarthomehub.module.trait.StartStop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@DeviceType("action.devices.types.VACUUM")
@Service("dreamme_vacuum_w10_pro")
public class Vacuum extends Device implements StartStop, Dock {
    @Override
    public void processDock(DocumentReference documentReference, Map<String, Object> params) {
        documentReference.update("states.dock", true);
    }

    @Override
    public void processStartStop(DocumentReference documentReference, Map<String, Object> params) {
        Boolean start = (Boolean) params.get("start");
        if (start){
            documentReference.update("states.isDocked", false);
            documentReference.update("states.isRunning", true);
        }else {
            documentReference.update("states.isRunning", false);
            documentReference.update("states.isDocked", true);
        }
    }
}

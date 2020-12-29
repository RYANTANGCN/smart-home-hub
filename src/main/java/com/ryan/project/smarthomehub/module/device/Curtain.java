package com.ryan.project.smarthomehub.module.device;

import com.google.cloud.firestore.DocumentSnapshot;
import com.ryan.project.smarthomehub.module.trait.OnOff;
import com.ryan.project.smarthomehub.module.trait.OpenClose;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Descritption
 * @Date 2020/12/25
 * @Author tangqianli
 */
@Slf4j
@Service("action.devices.types.CURTAIN")
public class Curtain extends IDevice implements OpenClose, OnOff {

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void processOnOff(DocumentSnapshot documentSnapshot, Map<String, Object> params) {

    }

    @Override
    public void processOpenClose(DocumentSnapshot documentSnapshot, Map<String, Object> params) {
        log.info("processOpenClose");
    }
}

package com.ryan.project.smarthomehub.module.device;

import com.google.cloud.firestore.DocumentReference;
import com.ryan.project.smarthomehub.module.trait.OpenClose;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Descritption
 * @Date 2020/12/25
 * @Author tangqianli
 */
@Slf4j
@Service("action.devices.types.CURTAIN")
public class Curtain extends Device implements OpenClose {

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void processOpenClose(DocumentReference documentReference, Map<String, Object> params) {
        log.debug("process curtain OpenClose traits,params:{}", params);
        int openPercent = (int) params.get("openPercent");
        List<Map<String, Object>> openState = new ArrayList<>();
        openState.add(new HashMap<>(){{put("openDirection","LEFT");put("openPercent",openPercent);}});
        openState.add(new HashMap<>(){{put("openDirection","RIGHT");put("openPercent",openPercent);}});
        documentReference.update("states.openState", openState);
    }
}

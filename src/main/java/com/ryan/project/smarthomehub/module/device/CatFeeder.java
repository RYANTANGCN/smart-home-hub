package com.ryan.project.smarthomehub.module.device;

import cn.hutool.core.map.MapUtil;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.ryan.project.smarthomehub.config.DeviceType;
import com.ryan.project.smarthomehub.module.trait.Dispense;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@DeviceType("action.devices.types.PETFEEDER")
@Service("esp_cat_food_feeder")
public class CatFeeder extends Device implements Dispense {
    @Override
    public Map<String, Object> processQuery(DocumentSnapshot device, Map<String, Object> customData) {
        List<Map<String, Object>> deviceState = (List<Map<String, Object>>) device.get("states");
        Map<String, Object> stateMap = MapUtil.of("dispenseItems", deviceState);
        stateMap.put("online", true);
        stateMap.put("status", "SUCCESS");
        return stateMap;
    }

    @Override
    public void processDispense(DocumentReference documentReference, Map<String, Object> params) {
        Integer amount = (Integer) params.get("amount");
        String unit = (String) params.get("CUPS");
        String item = (String) params.get("item");

        try {
            Integer amountRemaining = (Integer) documentReference.get().get().get("state.amountRemaining.amount");
            documentReference.update("state.amountRemaining.amount", amountRemaining - amount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}

package com.ryan.project.smarthomehub.module.fullfillment.service;

import com.google.api.services.homegraph.v1.HomeGraphService;
import com.google.api.services.homegraph.v1.model.ReportStateAndNotificationDevice;
import com.google.api.services.homegraph.v1.model.ReportStateAndNotificationRequest;
import com.google.api.services.homegraph.v1.model.ReportStateAndNotificationResponse;
import com.google.api.services.homegraph.v1.model.StateAndNotificationPayload;
import com.google.gson.Gson;
import com.ryan.project.smarthomehub.module.event.ReportStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class ReportStateListener implements ApplicationListener<ReportStateEvent> {

    @Autowired
    HomeGraphService homeGraphService;

    private Gson gson = new Gson();

    @Override
    public void onApplicationEvent(ReportStateEvent event) {
        String requestId = UUID.randomUUID().toString();
        ReportStateAndNotificationRequest request =
                new ReportStateAndNotificationRequest()
                        .setRequestId(requestId)
                        .setAgentUserId(event.getAgentUserId())
                        .setPayload(
                                new StateAndNotificationPayload()
                                        .setDevices(
                                                new ReportStateAndNotificationDevice()
                                                        .setStates(event.getStates())));

        try {
            log.info("report state payload: {}", gson.toJson(event.getStates()));
            ReportStateAndNotificationResponse response = homeGraphService.devices().reportStateAndNotification(request).execute();
            log.info("report state response: {}", response.toPrettyString());
        } catch (Exception e) {
            log.error("report state error, requestId: {}, event: {}:", requestId, event, e);
        }
    }
}

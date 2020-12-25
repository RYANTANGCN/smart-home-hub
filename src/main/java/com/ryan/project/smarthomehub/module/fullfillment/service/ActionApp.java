package com.ryan.project.smarthomehub.module.fullfillment.service;

import com.google.actions.api.smarthome.*;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.home.graph.v1.DeviceProto;
import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;
import com.ryan.project.smarthomehub.module.auth.service.ITokenService;
import com.ryan.project.smarthomehub.module.device.IDevice;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Descritption
 * @Date 2020/10/28
 * @Author tangqianli
 */
@Slf4j
@Service
public class ActionApp extends SmartHomeApp {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ITokenService tokenService;

    @Autowired
    Firestore database;

    @SneakyThrows
    @NotNull
    @Override
    public SyncResponse onSync(@NotNull SyncRequest syncRequest, @Nullable Map<?, ?> map) {

        //get userId
        String accessToken = (String) map.get("authorization");
        String userId = tokenService.getUserOpenId(accessToken);
//        String userId = "1234";

        log.debug("get userId:{} from access_token:{}", userId, accessToken);

        SyncResponse syncResponse = new SyncResponse(syncRequest.getRequestId(), new SyncResponse.Payload());
        syncResponse.payload.agentUserId = userId;
        List<QueryDocumentSnapshot> devices = database
                .collection("users")
                .document(userId)
                .collection("devices")
                .get().get().getDocuments();
        log.debug("query user:{} devices count:{}", userId, devices.size());
        syncResponse.payload.devices = new SyncResponse.Payload.Device[devices.size()];
        AtomicInteger i = new AtomicInteger(0);
        devices.forEach(w -> {
            QueryDocumentSnapshot device = w;
            SyncResponse.Payload.Device.Builder deviceBuilder =
                    new SyncResponse.Payload.Device.Builder()
                            .setId(device.getId())
                            .setType((String) device.get("type"))
                            .setTraits((List<String>) device.get("traits"))
                            .setName(
                                    DeviceProto.DeviceNames.newBuilder()
                                            .addAllDefaultNames((List<String>) device.get("defaultNames"))
                                            .setName((String) device.get("name"))
                                            .addAllNicknames((List<String>) device.get("nicknames"))
                                            .build())
                            .setWillReportState((Boolean) device.get("willReportState"))
                            .setRoomHint((String) device.get("roomHint"))
                            .setDeviceInfo(
                                    DeviceProto.DeviceInfo.newBuilder()
                                            .setManufacturer((String) device.get("manufacturer"))
                                            .setModel((String) device.get("model"))
                                            .setHwVersion((String) device.get("hwVersion"))
                                            .setSwVersion((String) device.get("swVersion"))
                                            .build());
            if (device.contains("attributes")) {
                Map<String, Object> attributes = new HashMap<>();
                attributes.putAll((Map<String, Object>) device.get("attributes"));
                String attributesJson = new Gson().toJson(attributes);
                Struct.Builder attributeBuilder = Struct.newBuilder();
                try {
                    JsonFormat.parser().ignoringUnknownFields().merge(attributesJson, attributeBuilder);
                } catch (Exception e) {
                    log.error("FAILED TO BUILD");
                }
                deviceBuilder.setAttributes(attributeBuilder.build());
            }
            /*if (device.contains("customData")) {
                Map<String, Object> customData = new HashMap<>();
                customData.putAll((Map<String, Object>) device.get("customData"));
                // TODO(proppy): remove once
                // https://github.com/actions-on-google/actions-on-google-java/issues/43 is fixed.
                String customDataJson = new Gson().toJson(customData);
                deviceBuilder.setCustomData(customDataJson);
            }
            if (device.contains("otherDeviceIds")) {
                deviceBuilder.setOtherDeviceIds((List) device.get("otherDeviceIds"));
            }*/
            syncResponse.payload.devices[i.get()] = deviceBuilder.build();
            i.incrementAndGet();
        });

        return syncResponse;
    }

    @NotNull
    @Override
    public QueryResponse onQuery(@NotNull QueryRequest queryRequest, @Nullable Map<?, ?> map) {
        //get userId
        String accessToken = (String) map.get("authorization");
        String userId = tokenService.getUserOpenId(accessToken);
//        String userId = "1234";
        log.debug("get userId:{} from access_token:{}", userId, accessToken);

        Map<String, Map<String, Object>> devicesState = new HashMap<>();

        Arrays.stream(queryRequest.inputs).forEach(w -> {
            if (w instanceof QueryRequest.Inputs) {
                Arrays.stream(((QueryRequest.Inputs) w).payload.devices).forEach(device -> {
                    try {
                        DocumentSnapshot documentSnapshot = database
                                .collection("users")
                                .document(userId)
                                .collection("devices").document(device.getId())
                                .get().get();
                        Map<String, Object> deviceState = (Map<String, Object>) documentSnapshot.get("states");
                        devicesState.put(device.getId(), deviceState);
                    } catch (InterruptedException e) {
                        devicesState.put(device.getId(), new HashMap<>());
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        devicesState.put(device.getId(), new HashMap<>());
                        e.printStackTrace();
                    }
                });
            }
        });
        QueryResponse queryResponse = new QueryResponse(queryRequest.getRequestId(), new QueryResponse.Payload());
        queryResponse.payload.setDevices(devicesState);
        return queryResponse;
    }

    @SneakyThrows
    @NotNull
    @Override
    public ExecuteResponse onExecute(@NotNull ExecuteRequest executeRequest, @Nullable Map<?, ?> map) {
        //get userId
        String accessToken = (String) map.get("authorization");
        String userId = tokenService.getUserOpenId(accessToken);

        for (SmartHomeRequest.RequestInputs input : executeRequest.inputs) {
            if (input instanceof ExecuteRequest.Inputs){

                for (ExecuteRequest.Inputs.Payload.Commands command : ((ExecuteRequest.Inputs) input).payload.commands) {
                    for (ExecuteRequest.Inputs.Payload.Commands.Devices device : command.devices) {
                        //TODO
                        DocumentSnapshot documentSnapshot = database
                                .collection("users")
                                .document(userId)
                                .collection("devices").document(device.getId())
                                .get().get();

                        IDevice concreteDevice = (IDevice) applicationContext.getBean((String) documentSnapshot.get("type"));

                        concreteDevice.processTraits(documentSnapshot, command.execution);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void onDisconnect(@NotNull DisconnectRequest disconnectRequest, @Nullable Map<?, ?> map) {
        //get userId
        String accessToken = (String) map.get("authorization");

        //revoke token store record
        tokenService.revokeRefreshToken(accessToken);
    }
}

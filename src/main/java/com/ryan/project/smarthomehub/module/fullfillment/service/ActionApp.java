package com.ryan.project.smarthomehub.module.fullfillment.service;

import com.google.actions.api.smarthome.*;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.home.graph.v1.DeviceProto;
import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;
import com.ryan.project.smarthomehub.config.DeviceType;
import com.ryan.project.smarthomehub.module.auth.service.ITokenService;
import com.ryan.project.smarthomehub.module.device.Device;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;
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
        devices.forEach(device -> {
            Map<String, Object> deviceInfo = (Map<String, Object>)device.getData().get("deviceInfo");
            Map<String, Object> nameObject = (Map<String, Object>) device.getData().get("name");
            SyncResponse.Payload.Device.Builder deviceBuilder =
                    new SyncResponse.Payload.Device.Builder()
                            .setId(device.getId())
                            .setType((String) device.get("type"))
                            .setTraits((List<String>) device.get("traits"))
                            .setName(
                                    DeviceProto.DeviceNames.newBuilder()
                                            .addAllDefaultNames((List<String>) nameObject.get("defaultNames"))
                                            .setName((String) nameObject.get("name"))
                                            .addAllNicknames((List<String>) nameObject.get("nicknames"))
                                            .build())
                            .setWillReportState((Boolean) device.get("willReportState"))
                            .setRoomHint((String) device.get("roomHint"))
                            .setDeviceInfo(
                                    DeviceProto.DeviceInfo.newBuilder()
                                            .setManufacturer((String) deviceInfo.get("manufacturer"))
                                            .setModel((String) deviceInfo.get("model"))
                                            .setHwVersion((String) deviceInfo.get("hwVersion"))
                                            .setSwVersion((String) deviceInfo.get("swVersion"))
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
            }*/
            if (device.contains("otherDeviceIds")) {
                deviceBuilder.setOtherDeviceIds((List) device.get("otherDeviceIds"));
            }
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
                        Map<String, Object> deviceInfo = (Map<String, Object>) documentSnapshot.get("deviceInfo");
                        String model = (String) deviceInfo.get("model");
                        Device concreteDevice = (Device) applicationContext.getBeansWithAnnotation(DeviceType.class).get(model);
                        /*concreteDevice.processQuery(documentSnapshot, device.getCustomData());
                        Map<String, Object> deviceState = (Map<String, Object>) documentSnapshot.get("states");*/
                        devicesState.put(device.getId(), concreteDevice.processQuery(documentSnapshot, device.getCustomData()));
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

        ExecuteResponse executeResponse = new ExecuteResponse();
        executeResponse.payload = new ExecuteResponse.Payload();
        List<ExecuteResponse.Payload.Commands> commandsResponse = new ArrayList<>();

        for (SmartHomeRequest.RequestInputs input : executeRequest.inputs) {
            if (input instanceof ExecuteRequest.Inputs){

                //Command List
                for (ExecuteRequest.Inputs.Payload.Commands command : ((ExecuteRequest.Inputs) input).payload.commands) {
                    ExecuteResponse.Payload.Commands respCommand = new ExecuteResponse.Payload.Commands();
                    respCommand.setStates(new HashMap<>());


                    List<String> respDeviceIds = new ArrayList<>();
                    //Execution List
                    for (ExecuteRequest.Inputs.Payload.Commands.Execution execution : command.execution) {

//                        respCommand.getStates().putAll(execution.getParams());
                        //Device List
                        for (ExecuteRequest.Inputs.Payload.Commands.Devices device : command.devices) {
                            DocumentReference documentReference = database
                                    .collection("users")
                                    .document(userId)
                                    .collection("devices").document(device.getId());
                            Map<String, Object> deviceInfo = (Map<String, Object>) documentReference.get().get().getData().get("deviceInfo");
                            String model = (String) deviceInfo.get("model");
                            Device concreteDevice = (Device) applicationContext.getBeansWithAnnotation(DeviceType.class).get(model);
                            if (concreteDevice == null) {
                                log.error(model + " not found");
                                continue;
                            }
//                            Device concreteDevice = (Device) applicationContext.getBean((String) documentReference.get().get().get("type"));

                            respDeviceIds.add(concreteDevice.processTraits(documentReference, execution));
                        }
                    }
                    respCommand.setIds(respDeviceIds.toArray(new String[respDeviceIds.size()]));
                    respCommand.setStatus("SUCCESS");
                    commandsResponse.add(respCommand);
                }
            }
        }
        executeResponse.payload.setCommands(commandsResponse.toArray(new ExecuteResponse.Payload.Commands[commandsResponse.size()]));
        executeResponse.setRequestId(executeRequest.getRequestId());
        return executeResponse;
    }

    @Override
    public void onDisconnect(@NotNull DisconnectRequest disconnectRequest, @Nullable Map<?, ?> map) {
        //get userId
        String accessToken = (String) map.get("authorization");

        //revoke token store record
        tokenService.revokeRefreshToken(accessToken);
    }
}

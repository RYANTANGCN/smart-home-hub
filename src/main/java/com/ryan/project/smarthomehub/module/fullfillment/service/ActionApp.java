package com.ryan.project.smarthomehub.module.fullfillment.service;

import com.google.actions.api.smarthome.*;
import com.google.gson.Gson;
import com.ryan.project.smarthomehub.module.auth.service.ITokenService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Descritption
 * @Date 2020/10/28
 * @Author tangqianli
 */
@Slf4j
@Service
public class ActionApp extends SmartHomeApp {

    @Autowired
    ITokenService tokenService;

    @NotNull
    @Override
    public SyncResponse onSync(@NotNull SyncRequest syncRequest, @Nullable Map<?, ?> map) {

        Gson gson = new Gson();

        log.debug("headers:{}", gson.toJson(map));
        //get userId
        String accessToken = (String) map.get("Authorization");
        String userId = tokenService.getUserOpenId(accessToken);

        SyncResponse syncResponse = new SyncResponse(syncRequest.getRequestId(), new SyncResponse.Payload());
        syncResponse.payload.agentUserId = userId;
        syncResponse.payload.devices = new SyncResponse.Payload.Device[0];

        return syncResponse;
    }

    @NotNull
    @Override
    public QueryResponse onQuery(@NotNull QueryRequest queryRequest, @Nullable Map<?, ?> map) {
        return null;
    }

    @NotNull
    @Override
    public ExecuteResponse onExecute(@NotNull ExecuteRequest executeRequest, @Nullable Map<?, ?> map) {
        return null;
    }

    @Override
    public void onDisconnect(@NotNull DisconnectRequest disconnectRequest, @Nullable Map<?, ?> map) {

    }
}

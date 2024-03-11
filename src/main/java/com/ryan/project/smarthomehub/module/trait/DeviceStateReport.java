package com.ryan.project.smarthomehub.module.trait;

public interface DeviceStateReport {

    void reportDeviceState(String userId, String deviceId, String message);
}

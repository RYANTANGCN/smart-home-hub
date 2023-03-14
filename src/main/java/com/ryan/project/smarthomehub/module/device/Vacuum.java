package com.ryan.project.smarthomehub.module.device;

import com.ryan.project.smarthomehub.config.DeviceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@DeviceType("action.devices.types.VACUUM")
@Service("dreamme_vacuum_w10_pro")
public class Vacuum extends Device {
}

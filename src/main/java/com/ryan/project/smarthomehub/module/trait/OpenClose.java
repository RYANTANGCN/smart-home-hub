package com.ryan.project.smarthomehub.module.trait;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.DocumentSnapshot;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Descritption
 * @Date 2020/12/25
 * @Author tangqianli
 */
@Slf4j
@Service("action.devices.commands.OpenClose")
public class OpenClose implements IExecution{
    @SneakyThrows
    @Override
    public String processExecution(DocumentSnapshot documentSnapshot, Map<String, Object> params) {
        ObjectMapper objectMapper = new ObjectMapper();
        log.info(objectMapper.writeValueAsString(params));
        return null;
    }
}

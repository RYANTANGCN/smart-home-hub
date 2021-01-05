package com.ryan.project.smarthomehub.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Descritption
 * @Date 2021/1/5
 * @Author tangqianli
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mqtt")
public class MqttProperties {

    private String broker;

    private String clientId;
}

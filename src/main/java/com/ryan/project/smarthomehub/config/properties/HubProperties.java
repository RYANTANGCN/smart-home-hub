package com.ryan.project.smarthomehub.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "smart-home-hub")
public class HubProperties {

    private String projectId;

    private String ssoUrl;

    private List<String> excludeUri;
}

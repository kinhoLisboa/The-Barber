package com.theBarber.TheBarber_notification.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "zapi")
@Getter
@Setter
public class ZapiProperties {

    private String baseUrl;
    private String instanceId;
    private String token;
    private String accountToken;
}

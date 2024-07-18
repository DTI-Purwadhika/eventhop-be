package com.riri.eventhop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration")
@Data
public class OAuth2ClientProperties {
    private Google google;
    @Data
    public static class Google {
        private String clientId;
        private String clientSecret;
    }

}
package com.example.demo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "google-cloud")
@Configuration
class GoogleCloudConfigurationBean {
    private Map<String, Function> functions;

    @Data
    @SuppressWarnings("WeakerAccess")
    public class Function {
        private String uri;
    }
}

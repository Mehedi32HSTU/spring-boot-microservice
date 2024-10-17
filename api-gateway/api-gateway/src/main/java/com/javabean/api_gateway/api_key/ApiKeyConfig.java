package com.javabean.api_gateway.api_key;

import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.Map;

@Configuration
public class ApiKeyConfig {
    @Bean
    public ApiKeyProperties apiKeyProperties() {
        ApiKeyProperties apiKeyProperties = new ApiKeyProperties();

        // Load the YAML file
        ClassPathResource resource = new ClassPathResource("api-key/api-key-properties.yaml");
        YamlMapFactoryBean yaml = new YamlMapFactoryBean();
        yaml.setResources(resource);

        // Extract the data from YAML and set it into the ApiKeyProperties POJO
        Map<String, Object> yamlData = yaml.getObject();
        if (yamlData != null) {
            Map<String, String> keys = (Map<String, String>) ((Map<String, Object>) yamlData.get("api")).get("keys");
            apiKeyProperties.setKeys(keys);
        }

        return apiKeyProperties;
    }
}

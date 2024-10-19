package com.javabean.api_gateway.filter.service_name;

import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.Map;

@Configuration
public class ServiceNameConfig {
    @Bean
    public ServiceNameProperties serviceNameProperties() {
        ServiceNameProperties serviceNameProperties = new ServiceNameProperties();

        // Load the YAML file
        ClassPathResource resource = new ClassPathResource("service-names/services-name.yaml");
        YamlMapFactoryBean yaml = new YamlMapFactoryBean();
        yaml.setResources(resource);

        // Extract the data from YAML and set it into the ApiKeyProperties POJO
        Map<String, Object> yamlData = yaml.getObject();
        if (yamlData != null) {
            Map<String, String> serviceNames = (Map<String, String>) ((Map<String, Object>) yamlData.get("service")).get("names");
            serviceNameProperties.setServiceNames(serviceNames);
        }
        return serviceNameProperties;
    }
}

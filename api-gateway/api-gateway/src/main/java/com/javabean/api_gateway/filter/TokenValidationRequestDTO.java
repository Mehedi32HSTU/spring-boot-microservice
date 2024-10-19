package com.javabean.api_gateway.filter;

import jakarta.validation.constraints.NotBlank;

public class TokenValidationRequestDTO {
    @NotBlank(message = "Please Provide Token.")
    private String token;
    @NotBlank(message = "Please Provide Request URL.")
    private String url;
    @NotBlank(message = "Please Provide Service Name.")
    private String serviceName;
    @NotBlank(message = "Please Provide Context Path.")
    private String contextPath;

    public TokenValidationRequestDTO() {
    }

    public TokenValidationRequestDTO(String token, String url, String serviceName, String contextPath) {
        this.token = token;
        this.url = url;
        this.serviceName = serviceName;
        this.contextPath = contextPath;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
}

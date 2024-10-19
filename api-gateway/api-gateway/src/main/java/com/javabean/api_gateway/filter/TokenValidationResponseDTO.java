package com.javabean.api_gateway.filter;

public class TokenValidationResponseDTO {
    private String token;
    private boolean isValid;
    private String message;
    private String url;
    private String serviceName;
    private String contextPath;

    public TokenValidationResponseDTO() {
    }

    public TokenValidationResponseDTO(String token, boolean isValid, String message, String url, String serviceName, String contextPath) {
        this.token = token;
        this.isValid = isValid;
        this.message = message;
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

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

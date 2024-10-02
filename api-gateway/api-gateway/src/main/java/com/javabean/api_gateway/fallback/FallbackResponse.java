package com.javabean.api_gateway.fallback;

public class FallbackResponse {
    private String message;
    public FallbackResponse() {}
    public FallbackResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

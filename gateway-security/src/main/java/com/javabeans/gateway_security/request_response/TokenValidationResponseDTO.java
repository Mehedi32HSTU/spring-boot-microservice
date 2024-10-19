package com.javabeans.gateway_security.request_response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenValidationResponseDTO {
    private String token;
    private boolean isValid;
    private String message;
    private String url;
    private String serviceName;
    private String contextPath;
}

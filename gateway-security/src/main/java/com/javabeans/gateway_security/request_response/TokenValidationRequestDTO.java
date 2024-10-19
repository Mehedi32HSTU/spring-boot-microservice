package com.javabeans.gateway_security.request_response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenValidationRequestDTO {
    @NotBlank(message = "Please Provide Token.")
    private String token;
    @NotBlank(message = "Please Provide Request URL.")
    private String url;
    @NotBlank(message = "Please Provide Service Name.")
    private String serviceName;
    @NotBlank(message = "Please Provide Context Path.")
    private String contextPath;
}

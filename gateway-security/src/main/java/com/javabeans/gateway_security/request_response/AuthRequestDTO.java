package com.javabeans.gateway_security.request_response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDTO {
    @NotBlank(message = "Please Provide a Username")
    private String username;
    @NotBlank(message = "Please Provide a Password")
    private String password;
}

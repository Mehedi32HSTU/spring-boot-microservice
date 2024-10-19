package com.javabeans.gateway_security.request_response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {
    private Long userId;
    private String firstname;
    private String lastname;
    private String username;
    private String token;
    private List<String> roles = new ArrayList<>();
}

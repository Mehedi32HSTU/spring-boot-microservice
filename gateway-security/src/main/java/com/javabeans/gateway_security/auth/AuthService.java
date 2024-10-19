package com.javabeans.gateway_security.auth;

import com.javabeans.gateway_security.request_response.AuthRequestDTO;
import com.javabeans.gateway_security.request_response.TokenValidationRequestDTO;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    public ResponseEntity<?> authenticateAndGetToken(AuthRequestDTO authRequestDTO);

    public ResponseEntity<?> validateToken(TokenValidationRequestDTO tokenValidationRequestDTO);
}

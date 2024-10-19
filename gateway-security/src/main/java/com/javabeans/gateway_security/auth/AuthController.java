package com.javabeans.gateway_security.auth;

import com.javabeans.gateway_security.request_response.AuthRequestDTO;
import com.javabeans.gateway_security.request_response.TokenValidationRequestDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody @Valid AuthRequestDTO authRequestDTO){

        return authService.authenticateAndGetToken(authRequestDTO);
    }
    @RequestMapping(value = "/validate-token", method = RequestMethod.POST)
    public ResponseEntity<?> validateToken(@RequestBody @Valid TokenValidationRequestDTO tokenValidationRequestDTO){

        return authService.validateToken(tokenValidationRequestDTO);
    }
}

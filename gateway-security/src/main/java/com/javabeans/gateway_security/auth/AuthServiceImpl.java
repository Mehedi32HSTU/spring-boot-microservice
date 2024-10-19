package com.javabeans.gateway_security.auth;

import com.javabeans.gateway_security.request_response.*;
import com.javabeans.gateway_security.security.jwt.JwtService;
import com.javabeans.gateway_security.security.services.CustomUserDetails;
import com.javabeans.gateway_security.security.users.UserInfo;
import com.javabeans.gateway_security.security.users.UserInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;
    @Override
    public ResponseEntity<?> authenticateAndGetToken(AuthRequestDTO authRequestDTO) {
        try {
            logger.info("<<<<<----------- authenticateAndGetToken is called ----------->>>>>");

            String username = authRequestDTO.getUsername();
            UserInfo userInfo = userInfoRepository.findByUsername(username);

            if(Objects.isNull(userInfo)) {
                logger.info("User : " + username + " Not Registered");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("User Not Registered"));
            }
            Authentication authentication = null;
            try {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
            } catch(BadCredentialsException ex) {
                logger.info("Bad credentials exception occurred.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Bad credentials exception occurred."));
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwtToken = jwtService.generateToken(userInfo, authentication);

            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            UserInfo logedInUserInfo = userInfoRepository.findByUsername(username);
            List<String> roles = customUserDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new AuthResponseDTO(
                    logedInUserInfo.getId(),
                    logedInUserInfo.getFirstname(),
                    logedInUserInfo.getLastname(),
                    logedInUserInfo.getUsername(),
                    jwtToken,
                    roles));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("An exception is occurred, reason : "+e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Bad credentials exception occurred."));
        }
    }

    @Override
    public ResponseEntity<?> validateToken(TokenValidationRequestDTO tokenValidationRequestDTO) {
        try {
            logger.info("<<<<<----------- validateToken is called ----------->>>>>");
            if(Objects.isNull(tokenValidationRequestDTO))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Token Object Not Found."));
            String token = tokenValidationRequestDTO.getToken();
            boolean isValidToken = jwtService.validateToken(token);
            if (isValidToken) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(getValidationResponseDTO(tokenValidationRequestDTO, isValidToken));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(getValidationResponseDTO(tokenValidationRequestDTO, isValidToken));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("An exception is occurred, reason : "+e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(getValidationResponseDTO(tokenValidationRequestDTO, false));
        }
    }

    private TokenValidationResponseDTO getValidationResponseDTO(TokenValidationRequestDTO requestDTO, boolean isValid) {
        return TokenValidationResponseDTO.builder()
                .token(requestDTO.getToken())
                .url(requestDTO.getUrl())
                .serviceName(requestDTO.getServiceName())
                .contextPath(requestDTO.getContextPath())
                .isValid(isValid)
                .message(isValid ? "Valid Token." : "Token Not Valid, Please Provide a Valid Token.")
                .build();
    }
}

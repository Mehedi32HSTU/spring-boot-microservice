package com.javabean.api_gateway.api_key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api-key")
public class ApiKeyController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String SERVICE_HEADER = "service-name";

    @Autowired
    private ApiKeyService apiKeyService;

    @RequestMapping(value = "/encrypt", method = RequestMethod.GET)
    public ResponseEntity<?> getEncryptedApiKey(ServerHttpRequest request) {
        try {
            logger.info("getEncryptedApiKey method is called.");
            Map<String, String> headers = request.getHeaders().toSingleValueMap();
            String serviceName = headers.get(SERVICE_HEADER);
            if(Objects.isNull(serviceName))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Invalid Service Name."));

            String apiKeyValue = apiKeyService.getApiKey(serviceName);
            if(Objects.isNull(apiKeyValue))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Invalid Service Name."));
            String encryptedKey = EncryptionUtil.encrypt(apiKeyValue);
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(encryptedKey));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception {} occurred in {} method.", e.getMessage(), "getEncryptedApiKey");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("An Exception Occurred, Please Try Again Later."));
        }
    }
}

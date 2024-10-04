# Spring Cloud Config Server & Client Setup

This guide provides step-by-step instructions for setting up a **Spring Cloud Config Server** and integrating it with client microservices. It also includes instructions on how to centralize Actuator configuration.

## 1. Setting Up the Config Server

### Step 1.1: Create a git repository with ```application.yml``` and add common configuration. 
Example:
```yml

com:
 javabeans:
   config:
     service:
#       name: CONFIG-SERVICE
#       port: 8900
       message: Hello From config, Updated message
   api:
     gateway:
#       service:
#         name: API-GATEWAY
#         port: 8090
   customer:
     controller:
       url: http://API-GATEWAY/customer-service/controller
#     service:
#       context-path: /customer-service
#       name: CUSTOMER-SERVICE
#       port: 9090
   product:
     controller:
       url: http://API-GATEWAY/product-service/controller
#     service:
#       context-path: /product-service
#       name: PRODUCT-SERVICE
#       port: 9091
   order:
     controller:
       url: http://API-GATEWAY/order-service/controller
#     service:
#       context-path: /order-service
#       name: ORDER-SERVICE
#       port: 9092

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    hostname: localhost
    
management:
  endpoints:
    web:
      exposure:
        include: "*"   # Expose all actuator endpoints
  endpoint:
    health:
      show-details: always  # Shows detailed info in the health endpoint


```

### Step 1.2: Add Dependencies in the `pom.xml`

To set up the Spring Cloud Config Server, include the following dependencies in the `pom.xml`:

```xml
<dependencies>
    <!-- Spring Cloud Config Server -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
    <!-- Optional: Spring Boot Actuator for monitoring -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <!-- Optional: Spring Boot Eureka Client for making it as a Discovery Client -->
    <dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>
</dependencies>

```
### Step 1.3: Set port and other configuration in ```application.yml```

```yml

spring:
  application:
    name: CONFIG-SERVER
  profiles:
    active: native  # Profile name
  cloud:
    config:
      server:
        native:
          search-locations: file:///D:/Practise_and_Developments/SpringBoot/cloud-config-server # Git Repository location
server:
  port: 8888

```
### Step 1.4: Enable Config Server by adding ```@EnableConfigServer```

## 2. Setting Up the Config Clients
_**Repeat this process for each client service**_

### Step 2.1: Add Dependencies in the `pom.xml`

To set up the Spring Cloud Config Client, include the following dependencies in the `pom.xml`:

```xml
<dependencies>
    <!-- Spring Cloud Config Client -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
        <version>4.1.3</version>
    </dependency>
    <!-- Spring Boot Bootstrap -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-bootstrap</artifactId>
        <version>4.1.4</version>
    </dependency>
    <!-- Spring Boot Actuator for monitoring -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <!-- Optional: Spring Boot Eureka Client for making it as a Discovery Client -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
</dependencies>

```
### Step 2.2: In each microservice, add configuration in ```application.properties``` to tell the service to fetch properties from the Config Server.
```properties

spring.cloud.config.uri=http://localhost:8888

```

### Step 2.3: Centralized Service or Configuration Class: Add an  `ApplicationProperties.java` to fetch the properties centrally, add `@Configuration` and  `@RefreshScope` on it.

```java

@Configuration
@RefreshScope
public class ApplicationProperties {
    @Value("${com.javabeans.customer.controller.url}")
    private String customerControllerUrl;

    @Value("${com.javabeans.product.controller.url}")
    private String productControllerUrl;

    public String getCustomerControllerUrl() {
        return customerControllerUrl;
    }

    public String getProductControllerUrl() {
        return productControllerUrl;
    }
}

```

### Step 2.4: Dependency Injection: Inject the dependency of `ApplicationProperties` where needed.

### Step 2.5: Don't forget to add actuator endpoints exposure, health and commit the changes to the git repository `application.yml` file.

```yml
management:
  endpoints:
    web:
      exposure:
        include: "*"   # Expose all actuator endpoints
  endpoint:
    health:
      show-details: always  # Shows detailed info in the health endpoint

```
### Step 2.6: Fetch Central Config Changes and Health Check: Fetch the updated values of centralized config file and view health by calling these APIs.

```
HEALTH CHECK:
GET: http://localhost:<specific-server-port>/<context-path>/actuator/health
Refresh Config:
POST: http://localhost:<specific-server-port>/<context-path>/actuator/refresh

```



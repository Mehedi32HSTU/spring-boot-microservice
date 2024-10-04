# Spring Boot API Documentation Setun with `swagger OpenAPI 3.0`

This guide provides step-by-step instructions for setting up a **swagger OpenAPI 3.0** for API Documentation

## 1. Setting Up the Dependency
Add the following dependency for Spring Boot 3.x
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.0.4</version>
</dependency>

```

## Step 2: Add `@OpenAPIDefinition` annotation to the `main` method class.

## Step 3: Hide the API that we want to hide:
To hide any API, need to add `@Hide` annotation above the method url. If we want to hide the full controller, then we need to add `@Hide` annotation above the controller class.

## Show API Based On User's Role: If we want to show some API for some special users, we can use role based API publication, in this approach, spring security is needed.

Example: If I want to show some API for `ADMIN` User only, then:

### Step 1: Create a Custom Annotation: Create a custom annotation to mark admin endpoints

```java

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Hidden // This will hide the endpoint by default
public @interface AdminApi {
}

```
### Step 2: Use Custom Annotations in Your Controller: Use the custom annotation to mark your admin endpoints:

```java

@RestController
@RequestMapping("/api")
public class MyController {

    @GetMapping("/public-endpoint")
    public String publicEndpoint() {
        return "This endpoint is accessible to everyone";
    }

    @AdminApi // Marked as an admin API
    @GetMapping("/admin-endpoint")
    public String adminEndpoint() {
        return "This endpoint is accessible to admins only";
    }
}

```
### Step 2: Customizing Swagger Configuration: You can modify the OpenAPI configuration to conditionally include or exclude paths based on your security requirements.

Here's an example of how you might set it up with springdoc-openapi:

```java

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("My API").version("1.0"));
    }

    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
            boolean isAdmin = role.contains("ROLE_ADMIN");

            // Hide admin endpoints if user is not admin
            if (!isAdmin && handlerMethod.getMethod().isAnnotationPresent(AdminApi.class)) {
                operation.setHidden(true);
            }
            return operation;
        };
    }
}

```
### Explanation:
* **Custom Annotations:** The @AdminApi annotation marks methods that are restricted to admin users. By default, these methods will be hidden in the documentation.

* **Operation Customizer:** The operationCustomizer bean examines the current user's roles. If the user is not an admin and tries to access an endpoint marked with @AdminApi, that endpoint will be hidden in the OpenAPI documentation.

* **OpenAPI Bean:** This sets up basic OpenAPI documentation information.

This approach allows you to manage API visibility based on user roles while leveraging the features of `springdoc-openapi`.

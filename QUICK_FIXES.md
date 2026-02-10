# Quick Fixes Guide

## How to Fix Critical Issues

### 1. Fix Hardcoded Credentials

**Current (EmployeeController.java):**
```java
private String userName = "dipali";
private String passWord = "1234";
```

**Fix - Add to application.properties:**
```properties
app.auth.username=${AUTH_USERNAME:admin}
app.auth.password=${AUTH_PASSWORD:changeme}
```

**Fix - Update EmployeeController.java:**
```java
@Value("${app.auth.username}")
private String userName;

@Value("${app.auth.password}")
private String passWord;
```

**Better Fix - Use Spring Security (Recommended):**

Add dependency to `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Create SecurityConfig.java:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/api/xml").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic();
        return http.build();
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
            .username("${app.auth.username}")
            .password("{noop}${app.auth.password}")  // Use BCrypt in production
            .roles("USER")
            .build();
        return new InMemoryUserDetailsManager(user);
    }
}
```

---

### 2. Fix Duplicate Repository Methods

**Current (EmployeeRepository.java):**
```java
Employee findByEmpid(String empid);
Employee findByempid(String empid);
```

**Fix:**
```java
Employee findByEmpid(String empid);  // Keep only this one
```

Update all usages in EmployeeServiceImpl.java:
- Line 69: Change `findByempid` to `findByEmpid`

---

### 3. Add Input Validation

**Current (Employee.java):**
```java
private String empid;
private String fname;
private int salary;
```

**Fix - Add validation annotations:**
```java
@NotBlank(message = "Employee ID is required")
@Size(min = 3, max = 20, message = "Employee ID must be between 3 and 20 characters")
private String empid;

@NotBlank(message = "Name is required")
@Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
private String fname;

@Min(value = 0, message = "Salary must be positive")
private int salary;
```

Add to EmployeeController.java before @RequestBody:
```java
public ResponseEntity<StandardResponse> addEmployee(
    @RequestHeader("username") String username,
    @RequestHeader("password") String password, 
    @Valid @RequestBody Employee employee) {  // Add @Valid
```

Add dependency to pom.xml (if not present):
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

---

### 4. Fix PdfGenerator Column Headers

**Current (PdfGenerator.java lines 58-62):**
```java
cell.setPhrase(new Phrase("ID", font));
table.addCell(cell);
cell.setPhrase(new Phrase("Emp Name", font));
table.addCell(cell);
cell.setPhrase(new Phrase("Email", font));  // Wrong!
table.addCell(cell);
```

**Fix:**
```java
cell.setPhrase(new Phrase("ID", font));
table.addCell(cell);
cell.setPhrase(new Phrase("Employee Name", font));
table.addCell(cell);
cell.setPhrase(new Phrase("Employee ID", font));  // Correct!
table.addCell(cell);
```

---

### 5. Externalize Database Configuration

**Add to application.properties:**
```properties
# Use environment variables with defaults
spring.datasource.url=${DATABASE_URL:jdbc:postgresql://localhost:5432/postgresdb}
spring.datasource.username=${DATABASE_USERNAME:postgres}
spring.datasource.password=${DATABASE_PASSWORD:root}
```

**Update docker-compose.yml environment variables:**
```yaml
environment:
  DATABASE_URL: jdbc:postgresql://postgres:5432/postgresdb
  DATABASE_USERNAME: postgres
  DATABASE_PASSWORD: root
  AUTH_USERNAME: dipali
  AUTH_PASSWORD: 1234
```

---

### 6. Update Date Types to Java 8 Time API

**Current (Employee.java):**
```java
import java.sql.Date;
private Date dob;
private Date doj;
```

**Fix:**
```java
import java.time.LocalDate;
import java.time.LocalDateTime;

private LocalDate dob;
private LocalDate doj;
private LocalDateTime createdat;
private LocalDateTime updatedat;
```

**Also update in EmployeeShadow.java:**
```java
import java.time.LocalDate;
import java.time.LocalDateTime;

private LocalDate dob;
private LocalDate doj;
private LocalDateTime createdat;
private LocalDateTime updatedat;
```

---

### 7. Remove Conflicting Logging Config

**Action:** Delete one of these files:
- `src/main/resources/log4j.properties` (DELETE THIS)
- Keep `src/main/resources/logback.xml`

**Reason:** Spring Boot uses Logback by default. Having both configs can cause conflicts.

---

### 8. Add Global Exception Handler

**Create new file: ExceptionHandlerAdvice.java**
```java
package com.employee.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.employee.response.StandardResponse;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
    
    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<StandardResponse> handleEmployeeNotFound(EmployeeNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new StandardResponse("error", ex.getMessage()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .reduce((a, b) -> a + ", " + b)
            .orElse("Validation failed");
            
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new StandardResponse("error", errorMessage));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse> handleGenericError(Exception ex) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new StandardResponse("error", "An unexpected error occurred: " + ex.getMessage()));
    }
}
```

---

### 9. Add Health Check Endpoint

**Add to pom.xml:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**Add to application.properties:**
```properties
# Actuator configuration
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
```

---

### 10. Remove @SuppressWarnings

**Find and remove these lines:**
- EmployeeController.java line 48: `@SuppressWarnings("unused")`
- EmployeeService.java line 10: `@SuppressWarnings("unused")`
- EmployeeServiceImpl.java line 31: `@SuppressWarnings("unused")`
- EmployeeRepository.java line 9: `@SuppressWarnings("unused")`

Then fix any actual unused imports or variables that appear.

---

## Testing the Fixes

### 1. Test with Docker
```bash
docker-compose down -v
docker-compose up --build
```

### 2. Test Endpoints
```bash
# Health check
curl http://localhost:8080/actuator/health

# Add employee (with validation)
curl -X POST http://localhost:8080/api/employees \
  -H "username: dipali" \
  -H "password: 1234" \
  -H "Content-Type: application/json" \
  -d '{
    "empid": "EMP001",
    "fname": "John Doe",
    "salary": 50000
  }'

# Test validation (should fail)
curl -X POST http://localhost:8080/api/employees \
  -H "username: dipali" \
  -H "password: 1234" \
  -H "Content-Type: application/json" \
  -d '{
    "empid": "",
    "fname": "A",
    "salary": -1000
  }'
```

---

## Priority Order

1. ✅ Externalize credentials (#1, #2, #5)
2. ✅ Fix duplicate methods (#2)
3. ✅ Add validation (#3)
4. ✅ Fix PDF headers (#4)
5. ✅ Add exception handler (#8)
6. ⚠️ Update date types (#6) - requires DB migration
7. ✅ Remove logging conflict (#7)
8. ✅ Add actuator (#9)
9. ✅ Remove suppressions (#10)

---

## After Applying Fixes

Run these commands to verify:

```bash
# Build the project
mvn clean package

# Run tests
mvn test

# Check for issues
mvn verify

# Run with Docker
docker-compose up --build
```

---

## Need Help?

If you encounter issues while applying these fixes:
1. Check the detailed error messages
2. Refer to CODE_REVIEW_REPORT.md for context
3. Check Spring Boot documentation
4. Ensure all dependencies are properly added to pom.xml

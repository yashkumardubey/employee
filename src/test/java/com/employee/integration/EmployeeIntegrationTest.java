package com.employee.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.employee.entity.Employee;
import com.employee.response.StandardResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@SuppressWarnings("null")
class EmployeeIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api";
        
        // Setup Basic Auth headers
        headers = new HttpHeaders();
        String auth = "yash:1234";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);
    }

    @Test
    void testFullEmployeeLifecycle() {
        // 1. Create a new employee
        Employee newEmployee = new Employee();
        newEmployee.setEmpid("EMPTEST001");
        newEmployee.setFname("TestUser");
        newEmployee.setSalary(55000);

        HttpEntity<Employee> createRequest = new HttpEntity<>(newEmployee, headers);
        ResponseEntity<StandardResponse> createResponse = restTemplate.exchange(
            baseUrl, 
            HttpMethod.POST, 
            createRequest, 
            StandardResponse.class
        );

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertEquals("success", createResponse.getBody().getStatus());

        // 2. Retrieve the employee by ID
        ResponseEntity<Employee> getResponse = restTemplate.getForEntity(
            baseUrl + "/EMPTEST001", 
            Employee.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals("EMPTEST001", getResponse.getBody().getEmpid());
        assertEquals("TestUser", getResponse.getBody().getFname());

        // 3. Search for employee by first name
        ResponseEntity<Employee[]> searchResponse = restTemplate.getForEntity(
            baseUrl + "/search/TestUser", 
            Employee[].class
        );

        assertEquals(HttpStatus.OK, searchResponse.getStatusCode());
        assertNotNull(searchResponse.getBody());
        assertTrue(searchResponse.getBody().length > 0);

        // 4. Update the employee
        Employee updateEmployee = new Employee();
        updateEmployee.setEmpid("EMPTEST001");
        updateEmployee.setFname("UpdatedUser");
        updateEmployee.setSalary(65000);

        HttpEntity<Employee> updateRequest = new HttpEntity<>(updateEmployee, headers);
        ResponseEntity<StandardResponse> updateResponse = restTemplate.exchange(
            baseUrl, 
            HttpMethod.PUT, 
            updateRequest, 
            StandardResponse.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals("success", updateResponse.getBody().getStatus());

        // 5. Verify the update
        ResponseEntity<Employee> verifyResponse = restTemplate.getForEntity(
            baseUrl + "/EMPTEST001", 
            Employee.class
        );

        assertEquals("UpdatedUser", verifyResponse.getBody().getFname());
        assertEquals(65000, verifyResponse.getBody().getSalary());

        // 6. Delete the employee
        HttpEntity<Void> deleteRequest = new HttpEntity<>(headers);
        ResponseEntity<StandardResponse> deleteResponse = restTemplate.exchange(
            baseUrl + "/UpdatedUser", 
            HttpMethod.DELETE, 
            deleteRequest, 
            StandardResponse.class
        );

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertEquals("success", deleteResponse.getBody().getStatus());
    }

    @Test
    void testUnauthorizedAccess() {
        Employee newEmployee = new Employee();
        newEmployee.setEmpid("EMPTEST002");
        newEmployee.setFname("Unauthorized");
        newEmployee.setSalary(50000);

        // Try to create without auth headers
        HttpEntity<Employee> request = new HttpEntity<>(newEmployee);
        ResponseEntity<StandardResponse> response = restTemplate.exchange(
            baseUrl, 
            HttpMethod.POST, 
            request, 
            StandardResponse.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("error", response.getBody().getStatus());
    }

    @Test
    void testGetAllEmployees() {
        ResponseEntity<Employee[]> response = restTemplate.getForEntity(
            baseUrl, 
            Employee[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // Response should be an array (even if empty)
        assertTrue(response.getBody().length >= 0);
    }

    @Test
    void testGetNonExistentEmployee() {
        ResponseEntity<StandardResponse> response = restTemplate.getForEntity(
            baseUrl + "/NONEXISTENT999", 
            StandardResponse.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("error", response.getBody().getStatus());
    }

    @Test
    void testSearchNonExistentName() {
        ResponseEntity<StandardResponse> response = restTemplate.getForEntity(
            baseUrl + "/search/NonExistentName999", 
            StandardResponse.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("error", response.getBody().getStatus());
    }
}

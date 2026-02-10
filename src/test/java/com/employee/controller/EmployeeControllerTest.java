package com.employee.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.employee.entity.Employee;
import com.employee.exception.EmployeeNotFoundException;
import com.employee.response.StandardResponse;
import com.employee.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(EmployeeController.class)
@SuppressWarnings("null")
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee testEmployee;
    private String authHeader;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee();
        testEmployee.setEmpid("EMP001");
        testEmployee.setFname("John");
        testEmployee.setSalary(50000);

        // Create Basic Auth header with username: yash, password: 1234
        authHeader = "Basic " + Base64.getEncoder().encodeToString("yash:1234".getBytes());
    }

    @Test
    void testAddEmployee_Success() throws Exception {
        StandardResponse response = new StandardResponse("success", "Employee details added successfully");
        when(employeeService.addEmployee(any(Employee.class))).thenReturn(response);

        mockMvc.perform(post("/api")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEmployee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void testAddEmployee_Unauthorized() throws Exception {
        mockMvc.perform(post("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEmployee)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void testGetEmployeeIdAndName_Success() throws Exception {
        List<Employee> employees = new ArrayList<>();
        employees.add(testEmployee);
        
        when(employeeService.getEmployeeIdAndFname()).thenReturn(employees);

        mockMvc.perform(get("/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].empid").value("EMP001"))
                .andExpect(jsonPath("$[0].fname").value("John"));
    }

    @Test
    void testGetEmployeeByEmpId_Found() throws Exception {
        when(employeeService.getEmployeeByEmpId("EMP001")).thenReturn(testEmployee);

        mockMvc.perform(get("/api/EMP001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empid").value("EMP001"))
                .andExpect(jsonPath("$.fname").value("John"));
    }

    @Test
    void testGetEmployeeByEmpId_NotFound() throws Exception {
        when(employeeService.getEmployeeByEmpId("EMP999")).thenReturn(null);

        mockMvc.perform(get("/api/EMP999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void testSearchByFirstName_Success() throws Exception {
        List<Employee> employees = new ArrayList<>();
        employees.add(testEmployee);
        
        when(employeeService.getEmployeesByFname("John")).thenReturn(employees);

        mockMvc.perform(get("/api/search/John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fname").value("John"));
    }

    @Test
    void testSearchByFirstName_NotFound() throws Exception {
        when(employeeService.getEmployeesByFname("Unknown")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/search/Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void testUpdateEmployee_Success() throws Exception {
        doNothing().when(employeeService).updateEmployeeDetails(anyString(), any(Employee.class));

        mockMvc.perform(put("/api")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEmployee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void testUpdateEmployee_NotFound() throws Exception {
        doThrow(new EmployeeNotFoundException("Employee not found"))
                .when(employeeService).updateEmployeeDetails(anyString(), any(Employee.class));

        mockMvc.perform(put("/api")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEmployee)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void testDeleteEmployeeByFname_Success() throws Exception {
        doNothing().when(employeeService).deleteEmployeeByFname("John");

        mockMvc.perform(delete("/api/John")
                .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void testDeleteEmployeeByFname_Unauthorized() throws Exception {
        mockMvc.perform(delete("/api/John"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"));
    }
}

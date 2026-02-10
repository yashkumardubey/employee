package com.employee.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.employee.entity.Employee;
import com.employee.entity.EmployeeShadow;
import com.employee.repository.EmployeeRepository;
import com.employee.repository.EmployeeShadowRepo;
import com.employee.response.StandardResponse;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeShadowRepo employeeShadowRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee();
        testEmployee.setEmpid("EMP001");
        testEmployee.setFname("John");
        testEmployee.setSalary(50000);
    }

    @Test
    void testAddEmployee_Success() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        StandardResponse response = employeeService.addEmployee(testEmployee);

        assertNotNull(response);
        assertEquals("success", response.getStatus());
        assertEquals("Employee details added successfully", response.getMessage());
        verify(employeeRepository, times(1)).save(testEmployee);
    }

    @Test
    void testGetEmployeeByEmpId_Found() {
        when(employeeRepository.findByEmpid("EMP001")).thenReturn(testEmployee);

        Employee result = employeeService.getEmployeeByEmpId("EMP001");

        assertNotNull(result);
        assertEquals("EMP001", result.getEmpid());
        assertEquals("John", result.getFname());
        verify(employeeRepository, times(1)).findByEmpid("EMP001");
    }

    @Test
    void testGetEmployeeByEmpId_NotFound() {
        when(employeeRepository.findByEmpid("EMP999")).thenReturn(null);

        Employee result = employeeService.getEmployeeByEmpId("EMP999");

        assertNull(result);
        verify(employeeRepository, times(1)).findByEmpid("EMP999");
    }

    @Test
    void testGetEmployeeIdAndFname() {
        List<Employee> employees = new ArrayList<>();
        employees.add(testEmployee);
        
        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.getEmployeeIdAndFname();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("EMP001", result.get(0).getEmpid());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void testGetEmployeesByFname_Found() {
        List<Employee> employees = new ArrayList<>();
        testEmployee.setFname("John");
        employees.add(testEmployee);
        
        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.getEmployeesByFname("John");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFname());
    }

    @Test
    void testGetEmployeesByFname_CaseInsensitive() {
        List<Employee> employees = new ArrayList<>();
        testEmployee.setFname("John");
        employees.add(testEmployee);
        
        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.getEmployeesByFname("JOHN");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFname());
    }

    @Test
    void testGetEmployeesByFname_NotFound() {
        when(employeeRepository.findAll()).thenReturn(new ArrayList<>());

        List<Employee> result = employeeService.getEmployeesByFname("Unknown");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testDeleteEmployeeByFname_Success() {
        when(employeeRepository.findByfname("John")).thenReturn(testEmployee);
        when(employeeShadowRepository.save(any(EmployeeShadow.class))).thenReturn(new EmployeeShadow());
        doNothing().when(employeeRepository).delete(testEmployee);

        employeeService.deleteEmployeeByFname("John");

        verify(employeeRepository, times(1)).findByfname("John");
        verify(employeeShadowRepository, times(1)).save(any(EmployeeShadow.class));
        verify(employeeRepository, times(1)).delete(testEmployee);
    }

    @Test
    void testDeleteEmployeeByFname_EmployeeNotFound() {
        when(employeeRepository.findByfname("Unknown")).thenReturn(null);

        employeeService.deleteEmployeeByFname("Unknown");

        verify(employeeRepository, times(1)).findByfname("Unknown");
        verify(employeeShadowRepository, never()).save(any(EmployeeShadow.class));
        verify(employeeRepository, never()).delete(any(Employee.class));
    }

    @Test
    void testUpdateEmployeeDetails_Success() {
        Employee updatedEmployee = new Employee();
        updatedEmployee.setEmpid("EMP001");
        updatedEmployee.setFname("Jane");
        updatedEmployee.setSalary(60000);

        when(employeeRepository.findByEmpid("EMP001")).thenReturn(testEmployee);
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        employeeService.updateEmployeeDetails("EMP001", updatedEmployee);

        assertEquals("Jane", testEmployee.getFname());
        assertEquals(60000, testEmployee.getSalary());
        verify(employeeRepository, times(1)).findByEmpid("EMP001");
        verify(employeeRepository, times(1)).save(testEmployee);
    }

    @Test
    void testUpdateEmployeeDetails_EmployeeNotFound() {
        Employee updatedEmployee = new Employee();
        updatedEmployee.setEmpid("EMP999");

        when(employeeRepository.findByEmpid("EMP999")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            employeeService.updateEmployeeDetails("EMP999", updatedEmployee);
        });

        assertTrue(exception.getMessage().contains("Employee not found"));
        verify(employeeRepository, times(1)).findByEmpid("EMP999");
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void testGetAllExportEmployees() {
        List<Employee> employees = new ArrayList<>();
        employees.add(testEmployee);
        
        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.getAllExportEmployees();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void testGetAllExcelEmployees() {
        List<Employee> employees = new ArrayList<>();
        employees.add(testEmployee);
        
        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.getAllExcelEmployees();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeeRepository, times(1)).findAll();
    }
}

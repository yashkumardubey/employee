package com.employee.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.employee.entity.Employee;
import com.employee.entity.EmployeeClone;
import com.employee.response.StandardResponse;

public interface EmployeeService {
	
	 StandardResponse addEmployee(Employee employee);
	 
	 void updateEmployeeDetails(String empid, Employee updatedEmployee);
	
	 Employee getEmployeeByEmpId(String empid);
	 
	 void deleteEmployeeByFname(String fname);
	 
	 ResponseEntity<?> getEmployeeIdAndFname();
	 
	 //xml
	 
	 ResponseEntity<?> getEmployeeIdName();
	 
	 
	List<Employee> getAllExportEmployees();
	 
	 
	 List<Employee> getAllExcelEmployees();
	
}

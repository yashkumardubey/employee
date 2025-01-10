package com.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee.entity.Employee;

@SuppressWarnings("unused")
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
	
	Employee findByEmpid(String empid);
	
	 Employee findByempid(String empid);
	 void deleteByfname(String fname);
	 Employee findByfname(String fname);
		
	 
// Employee findByEmpId(String empid);

}

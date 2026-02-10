package com.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
	
	Employee findByEmpid(String empid);
	
	void deleteByfname(String fname);
	Employee findByfname(String fname);

}

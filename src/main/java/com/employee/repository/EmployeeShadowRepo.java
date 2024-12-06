
  package com.employee.repository;
  
  import org.springframework.data.jpa.repository.JpaRepository;
  
  import com.employee.entity.Employee; import
  com.employee.entity.EmployeeShadow;
  
  public interface EmployeeShadowRepo extends JpaRepository<EmployeeShadow,
  Integer>{
  
  Employee save(Employee existingEmployee);
  
  }
 
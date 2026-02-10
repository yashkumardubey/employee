package com.employee.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.employee.entity.Employee;

@DataJpaTest
@SuppressWarnings("null")
class EmployeeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee();
        testEmployee.setEmpid("EMP001");
        testEmployee.setFname("John");
        testEmployee.setSalary(50000);
    }

    @Test
    void testFindByEmpid_Found() {
        entityManager.persist(testEmployee);
        entityManager.flush();

        Employee found = employeeRepository.findByEmpid("EMP001");

        assertNotNull(found);
        assertEquals("EMP001", found.getEmpid());
        assertEquals("John", found.getFname());
    }

    @Test
    void testFindByEmpid_NotFound() {
        Employee found = employeeRepository.findByEmpid("EMP999");

        assertNull(found);
    }

    @Test
    void testFindByfname_Found() {
        entityManager.persist(testEmployee);
        entityManager.flush();

        Employee found = employeeRepository.findByfname("John");

        assertNotNull(found);
        assertEquals("John", found.getFname());
        assertEquals("EMP001", found.getEmpid());
    }

    @Test
    void testFindByfname_NotFound() {
        Employee found = employeeRepository.findByfname("Unknown");

        assertNull(found);
    }

    @Test
    void testSaveEmployee() {
        Employee saved = employeeRepository.save(testEmployee);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("EMP001", saved.getEmpid());
        assertEquals("John", saved.getFname());
    }

    @Test
    void testFindAll() {
        Employee employee1 = new Employee();
        employee1.setEmpid("EMP001");
        employee1.setFname("John");
        employee1.setSalary(50000);

        Employee employee2 = new Employee();
        employee2.setEmpid("EMP002");
        employee2.setFname("Jane");
        employee2.setSalary(60000);

        entityManager.persist(employee1);
        entityManager.persist(employee2);
        entityManager.flush();

        List<Employee> employees = employeeRepository.findAll();

        assertNotNull(employees);
        assertEquals(2, employees.size());
    }

    @Test
    void testDeleteEmployee() {
        Employee saved = entityManager.persist(testEmployee);
        entityManager.flush();

        employeeRepository.delete(saved);

        Employee found = employeeRepository.findByEmpid("EMP001");
        assertNull(found);
    }
}

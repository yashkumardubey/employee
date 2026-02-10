package com.employee.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmployeeTest {

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
    }

    @Test
    void testSetAndGetEmpid() {
        employee.setEmpid("EMP001");
        assertEquals("EMP001", employee.getEmpid());
    }

    @Test
    void testSetAndGetFname() {
        employee.setFname("John");
        assertEquals("John", employee.getFname());
    }

    @Test
    void testSetAndGetSalary() {
        employee.setSalary(50000);
        assertEquals(50000, employee.getSalary());
    }

    @Test
    void testSetAndGetDob() {
        Date dob = Date.valueOf("1990-01-01");
        employee.setDob(dob);
        assertEquals(dob, employee.getDob());
    }

    @Test
    void testSetAndGetDoj() {
        Date doj = Date.valueOf("2020-01-01");
        employee.setDoj(doj);
        assertEquals(doj, employee.getDoj());
    }

    @Test
    void testSetAndGetReportsto() {
        employee.setReportsto(100);
        assertEquals(100, employee.getReportsto());
    }

    @Test
    void testSetAndGetDeptid() {
        employee.setDeptid(10);
        assertEquals(10, employee.getDeptid());
    }

    @Test
    void testSetAndGetRankid() {
        employee.setRankid(5);
        assertEquals(5, employee.getRankid());
    }

    @Test
    void testEmployeeValidation() {
        employee.setEmpid("EM");
        assertNotNull(employee.getEmpid());
        assertTrue(employee.getEmpid().length() < 3);
    }

    @Test
    void testEmployeeNullValues() {
        assertNull(employee.getEmpid());
        assertNull(employee.getFname());
        assertEquals(0, employee.getSalary());
        assertNull(employee.getDob());
        assertNull(employee.getDoj());
    }
}

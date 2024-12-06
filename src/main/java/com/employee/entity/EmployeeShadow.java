package com.employee.entity;
import java.util.Date;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "employee_shadow")
public class EmployeeShadow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String empid;
    private String fname;
    private Date dob;
    private Date doj;
    private int salary;
    private Integer reportsto;
    private Integer deptid;
    private Integer rankid;
    private Date createdat;
   // private Date updatedat;
    private String client_reqid;
    
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedat;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmpid() {
		return empid;
	}
	public void setEmpid(String empid) {
		this.empid = empid;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public Date getDoj() {
		return doj;
	}
	public void setDoj(Date doj) {
		this.doj = doj;
	}
	public int getSalary() {
		return salary;
	}
	public void setSalary(int salary) {
		this.salary = salary;
	}
	public Integer getReportsto() {
		return reportsto;
	}
	public void setReportsto(Integer reportsto) {
		this.reportsto = reportsto;
	}
	public Integer getDeptid() {
		return deptid;
	}
	public void setDeptid(Integer deptid) {
		this.deptid = deptid;
	}
	public Integer getRankid() {
		return rankid;
	}
	public void setRankid(Integer rankid) {
		this.rankid = rankid;
	}
	public Date getCreatedat() {
		return createdat;
	}
	public void setCreatedat(Date createdat) {
		this.createdat = createdat;
	}
	public Date getUpdatedat() {
		return updatedat;
	}
	public void setUpdatedat(Date updatedat) {
		this.updatedat = updatedat;
	}
	public String getClient_reqid() {
		return client_reqid;
	}
	public void setClient_reqid(String client_reqid) {
		this.client_reqid = client_reqid;
	}
	public EmployeeShadow(Long id, String empid, String fname, Date dob, Date doj, int salary, Integer reportsto,
			Integer deptid, Integer rankid, Date createdat, Date updatedat, String client_reqid) {
		super();
		this.id = id;
		this.empid = empid;
		this.fname = fname;
		this.dob = dob;
		this.doj = doj;
		this.salary = salary;
		this.reportsto = reportsto;
		this.deptid = deptid;
		this.rankid = rankid;
		this.createdat = createdat;
		this.updatedat = updatedat;
		this.client_reqid = client_reqid;
	}
	public EmployeeShadow() {
		super();
		// TODO Auto-generated constructor stub
	}

   
}

package com.employee.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.employee.controller.EmployeeController;
import com.employee.entity.Employee;
import com.employee.entity.EmployeeClone;
import com.employee.entity.EmployeeShadow;
//import com.employee.entity.EmployeeShadow;
import com.employee.exception.EmployeeNotFoundException;
import com.employee.repository.EmployeeRepository;
import com.employee.repository.EmployeeShadowRepo;
//import com.employee.repository.EmployeeShadowRepo;
import com.employee.response.HttpResponse;
import com.employee.response.StandardResponse;

import ch.qos.logback.classic.Logger;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@SuppressWarnings("unused")
@Service
public class EmployeeServiceImpl implements EmployeeService {
	
	private static final Logger log = (Logger) LoggerFactory.getLogger(EmployeeService.class);


	/*
	 * @Autowired private EmployeeRepository employeeRepository;
	 * 
	 */

    private final EmployeeRepository employeeRepository;
    private final EmployeeShadowRepo   employeeShadowRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeShadowRepo employeeShadowRepository) {
        this.employeeRepository = employeeRepository;
        this.employeeShadowRepository = employeeShadowRepository;
    }
   
    
    public StandardResponse addEmployee(Employee employee) {
        Employee savedEmployee = employeeRepository.save(employee);
        if (savedEmployee != null) {
            return new StandardResponse("success", "Employee details added successfully");
        } else {
            return new StandardResponse("error", "Failed to add employee");
        }
    
    }
    
	
           
        
    
    @Override
    public Employee getEmployeeByEmpId(String empid) {
        return employeeRepository.findByempid(empid);
    }
    
    
	/*
	 * 
	 * @Override
	 * 
	 * @Transactional public void deleteEmployeeByFname(String fname) {
	 * employeeRepository.deleteByfname(fname); }
	 */    
    
    
    @Override
    @Transactional
    public void deleteEmployeeByFname(String fname) {
        Employee employee = employeeRepository.findByfname(fname);

        if (employee != null) {
            EmployeeShadow shadow = new EmployeeShadow();
            shadow.setEmpid(employee.getEmpid());
            shadow.setFname(employee.getFname());
            shadow.setDob(employee.getDob());
            shadow.setDoj(employee.getDoj());
            shadow.setSalary(employee.getSalary());
            shadow.setReportsto(employee.getReportsto());
            shadow.setDeptid(employee.getDeptid());
            shadow.setRankid(employee.getRankid());
            shadow.setCreatedat(employee.getCreatedat());
            shadow.setUpdatedat(employee.getUpdatedat());
            shadow.setClient_reqid(employee.getClient_reqid());

            // Save to shadow table
            employeeShadowRepository.save(shadow);

            // Delete from employee table
            employeeRepository.delete(employee);
        }
    }
    
    
  
    @Override
    public ResponseEntity<?> getEmployeeIdAndFname() {
        List<Employee> empList = employeeRepository.findAll();

        if (empList.isEmpty()) {
            throw new EmployeeNotFoundException("Employee details not found");
        } else {
            
            List<Map<String, Object>> map = new ArrayList<>();

            for (Employee employee : empList) {
                Map<String, Object> emp = new HashMap<>();
                emp.put("empid", employee.getEmpid());
                emp.put("fname", employee.getFname());

                map.add(emp);
            }

            return HttpResponse.generateResponse("success", HttpStatus.OK, map);
        }
    }
    
    
    
    
    //XML 
    
    
    @Override
    public ResponseEntity<?> getEmployeeIdName() {
        List<Employee> empList = employeeRepository.findAll();

        if (empList.isEmpty()) {
            throw new EmployeeNotFoundException("Employee details not found");
        } else {
            
            List<Map<String, Object>> map = new ArrayList<>();

            for (Employee employee : empList) {
                Map<String, Object> emp = new HashMap<>();
                emp.put("empid", employee.getEmpid());
                emp.put("fname", employee.getFname());

                map.add(emp);
            }

            return HttpResponse.generateResponse("success", HttpStatus.OK, map);
        }
    }



   /* @Override
    @Transactional
    public void updateEmployeeDetails(String empid, Employee updatedEmployee) {
        Employee existingEmployee = employeeRepository.findByEmpid(empid);
        if (existingEmployee == null) {
            throw new RuntimeException("Employee not found with empid: " + empid);
        }

        existingEmployee.setFname(updatedEmployee.getFname());
        existingEmployee.setDob(updatedEmployee.getDob());
        existingEmployee.setDoj(updatedEmployee.getDoj());
        existingEmployee.setSalary(updatedEmployee.getSalary());
        existingEmployee.setReportsto(updatedEmployee.getReportsto());
        existingEmployee.setDeptid(updatedEmployee.getDeptid());
        existingEmployee.setRankid(updatedEmployee.getRankid());
        existingEmployee.setUpdatedat(updatedEmployee.getUpdatedat());
        employeeRepository.save(existingEmployee);
    }
*/ 
    
    @Transactional
    public void updateEmployeeDetails(String empid, Employee updatedEmployee) {
        log.info("Attempting to update employee with empid: {}", empid);

        try {
            Employee existingEmployee = employeeRepository.findByEmpid(empid);
            if (existingEmployee == null) {
                log.info("Employee not found with empid: {}", empid);
                throw new RuntimeException("Employee not found with empid: " + empid);
            }

            existingEmployee.setFname(updatedEmployee.getFname());
            existingEmployee.setDob(updatedEmployee.getDob());
            existingEmployee.setDoj(updatedEmployee.getDoj());
            existingEmployee.setSalary(updatedEmployee.getSalary());
            existingEmployee.setReportsto(updatedEmployee.getReportsto());
            existingEmployee.setDeptid(updatedEmployee.getDeptid());
            existingEmployee.setRankid(updatedEmployee.getRankid());
            existingEmployee.setUpdatedat(updatedEmployee.getUpdatedat());

            employeeRepository.save(existingEmployee);
            log.info("Successfully updated employee with empid: {}", empid);
        } catch (Exception e) {
            log.error("Error occurred while updating employee with empid: {}", empid, e);
            throw e;
        }
    }


    
	
	  public List<Employee> getAllExportEmployees() { return
	 employeeRepository.findAll(); }
	 
    
    
    
    
    public List<Employee> getAllExcelEmployees() {
        return employeeRepository.findAll();
    }
    
    
    
    
    
	/*
	 * @Override public void generate(List<Employee> empList, HttpServletResponse
	 * response) throws DocumentException, IOException { // Creating the Object of
	 * Document Document document = new Document(PageSize.A4); // Getting instance
	 * of PdfWriter PdfWriter.getInstance(document, response.getOutputStream()); //
	 * Opening the created document to change it document.open(); // Creating font
	 * // Setting font style and size Font fontTitle = new Font(Font.HELVETICA, 20,
	 * Font.BOLD); // Creating paragraph Paragraph paragraph1 = new
	 * Paragraph("List of Employees", fontTitle); // Aligning the paragraph in the
	 * document paragraph1.setAlignment(Paragraph.ALIGN_CENTER); // Adding the
	 * created paragraph in the document document.add(paragraph1); // Creating a
	 * table of the appropriate number of columns PdfPTable table = new
	 * PdfPTable(10); // Adjust the number of columns as per your entity fields //
	 * Setting width of the table, its columns, and spacing
	 * table.setWidthPercentage(100); table.setWidths(new float[]{1f, 2f, 3f, 2f,
	 * 2f, 1.5f, 2f, 1f, 1f, 2.5f}); // Adjust column widths as per your requirement
	 * table.setSpacingBefore(5); // Create Table Cells for the table header
	 * PdfPCell cell = new PdfPCell(); // Setting the background color and padding
	 * of the table cell cell.setBackgroundColor(new Color(0, 100, 255));
	 * cell.setPadding(5); // Creating font for header Font fontHeader = new
	 * Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE); // Adding headings in the
	 * created table cell or header // Adjust headings as per your entity fields
	 * cell.setPhrase(new com.lowagie.text.Phrase("Emp ID", fontHeader));
	 * table.addCell(cell); cell.setPhrase(new com.lowagie.text.Phrase("First Name",
	 * fontHeader)); table.addCell(cell); cell.setPhrase(new
	 * com.lowagie.text.Phrase("Full Name", fontHeader)); table.addCell(cell);
	 * cell.setPhrase(new com.lowagie.text.Phrase("DOB", fontHeader));
	 * table.addCell(cell); cell.setPhrase(new com.lowagie.text.Phrase("DOJ",
	 * fontHeader)); table.addCell(cell); cell.setPhrase(new
	 * com.lowagie.text.Phrase("Salary", fontHeader)); table.addCell(cell);
	 * cell.setPhrase(new com.lowagie.text.Phrase("Reports To", fontHeader));
	 * table.addCell(cell); cell.setPhrase(new com.lowagie.text.Phrase("Dept ID",
	 * fontHeader)); table.addCell(cell); cell.setPhrase(new
	 * com.lowagie.text.Phrase("Rank ID", fontHeader)); table.addCell(cell);
	 * cell.setPhrase(new com.lowagie.text.Phrase("Create Date", fontHeader));
	 * table.addCell(cell);
	 * 
	 * // Date formatter to convert dates to "1-08-2021" format DateTimeFormatter
	 * dateFormatter = DateTimeFormatter.ofPattern("d-MM-yyyy"); DateTimeFormatter
	 * dateTimeFormatter = DateTimeFormatter.ofPattern("d-MM-yyyy HH:mm:ss");
	 * 
	 * // Iterating the list of employees for (Employee emp : empList) {
	 * table.addCell(String.valueOf(emp.getEmpid())); table.addCell(emp.getFname());
	 * table.addCell(emp.getFullname());
	 * 
	 * // Format DOB if (emp.getDob() != null) {
	 * table.addCell(emp.getDob().format(dateFormatter)); } else {
	 * table.addCell(""); // Handle case where DOB is null }
	 * 
	 * // Format DOJ if (emp.getDoj() != null) {
	 * table.addCell(emp.getDoj().format(dateFormatter)); } else {
	 * table.addCell(""); // Handle case where DOJ is null }
	 * 
	 * table.addCell(String.valueOf(emp.getSalary()));
	 * table.addCell(emp.getReportsto() != null ? String.valueOf(emp.getReportsto())
	 * : ""); table.addCell(String.valueOf(emp.getDeptid()));
	 * table.addCell(String.valueOf(emp.getRankid()));
	 * 
	 * // Format Create Date if (emp.getCreatedate() != null) {
	 * table.addCell(emp.getCreatedate().format(dateTimeFormatter)); } else {
	 * table.addCell(""); // Handle case where Create Date is null } }
	 * 
	 * // Adding the created table to the document document.add(table); // Closing
	 * the document document.close(); }
	 * 
	 * 
	 */    
    
}
    

    
    
    
       





    

    
    


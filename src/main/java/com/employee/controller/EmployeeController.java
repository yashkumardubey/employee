package com.employee.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.employee.entity.Employee;
import com.employee.exception.EmployeeNotFoundException;
import com.employee.response.OutputResponse;
import com.employee.response.StandardResponse;
import com.employee.service.EmployeeExcelExporter;
import com.employee.service.EmployeeService;
import com.employee.service.PdfGenerator;
import com.lowagie.text.DocumentException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

	@Value("${app.auth.username}")
	private String userName;
	
	@Value("${app.auth.password}")
	private String passWord;
	
	// Helper method to validate Basic Authentication
	private boolean validateBasicAuth(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return false;
        }
        try {
            String encodedAuth = authHeader.substring(6);
            String decodedAuth = new String(Base64.getDecoder().decode(encodedAuth));
            String[] credentials = decodedAuth.split(":");
            if (credentials.length != 2) return false;
            return userName.equals(credentials[0]) && passWord.equals(credentials[1]);
        } catch (Exception e) {
            return false;
        }
    }

    @PostMapping("")
    public ResponseEntity<StandardResponse> addEmployee(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody Employee employee) {
        StandardResponse response;
        HttpStatus status;

        if (!validateBasicAuth(authHeader)) {
            log.warn("Unauthorized access attempt");
            response = new StandardResponse("error", "Invalid credentials");
            status = HttpStatus.UNAUTHORIZED;
        } else {
            try {
                response = employeeService.addEmployee(employee);
                status = response.getStatus().equals("success") ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            } catch (DataAccessException e) {
                log.error("Failed to perform database operation", e);
                response = new StandardResponse("error", "Failed to perform database operation. Please try again later.");
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            } catch (Exception e) {
                log.error("Unexpected error occurred", e);
                response = new StandardResponse("error", "An unexpected error occurred. Please try again later.");
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
        return new ResponseEntity<>(response, status);
    }

    @GetMapping("")
    public ResponseEntity<?> getEmployeeIdAndName() {
        try {
            List<Employee> empList = employeeService.getEmployeeIdAndFname();
            if (empList == null || empList.isEmpty()) {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
            }
            return new ResponseEntity<>(empList, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching employees", e);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
    }

    @GetMapping("/{empid}")
    public ResponseEntity<?> getEmployeeByEmpId(@PathVariable String empid) {
        log.info("Received request to get employee with empid: {}", empid);
        Object response;
        HttpStatus status;

        try {
            Employee employee = employeeService.getEmployeeByEmpId(empid);
            if (employee != null) {
                log.info("Employee found with empid: {}", empid);
                response = employee;
                status = HttpStatus.OK;
            } else {
                log.info("Employee with empid {} does not exist", empid);
                response = new StandardResponse("error", "Employee with empid " + empid + " does not exist in the database");
                status = HttpStatus.NOT_FOUND;
            }
        } catch (Exception e) {
            log.error("Error occurred while getting employee with empid: {}", empid, e);
            response = new StandardResponse("error", "An error occurred: " + e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/search/{fname}")
    public ResponseEntity<?> searchByFirstName(@PathVariable String fname) {
        log.info("Received request to search employees with fname: {}", fname);
        try {
            List<Employee> employees = employeeService.getEmployeesByFname(fname);
            if (employees != null && !employees.isEmpty()) {
                log.info("Found {} employees with fname: {}", employees.size(), fname);
                return new ResponseEntity<>(employees, HttpStatus.OK);
            } else {
                log.info("No employees found with fname: {}", fname);
                return new ResponseEntity<>(new StandardResponse("error", "No employees found with fname: " + fname), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error occurred while searching employees with fname: {}", fname, e);
            return new ResponseEntity<>(new StandardResponse("error", "An error occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("")
    public ResponseEntity<?> updateEmployee(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody Employee updatedEmployee) {
        log.info("Received request to update employee with empid: {}", updatedEmployee.getEmpid());
        StandardResponse response;
        HttpStatus status;

        if (!validateBasicAuth(authHeader)) {
            log.warn("Unauthorized access attempt");
            response = new StandardResponse("error", "Invalid credentials");
            status = HttpStatus.UNAUTHORIZED;
        } else {
            try {
                employeeService.updateEmployeeDetails(updatedEmployee.getEmpid(), updatedEmployee);
                log.info("Employee details updated successfully for empid: {}", updatedEmployee.getEmpid());
                response = new StandardResponse("success", "Employee details updated successfully");
                status = HttpStatus.OK;
            } catch (EmployeeNotFoundException e) {
                log.info("EmployeeNotFoundException: {}", e.getMessage());
                response = new StandardResponse("error", e.getMessage());
                status = HttpStatus.NOT_FOUND;
            } catch (Exception e) {
                log.error("Error occurred while updating employee", e);
                response = new StandardResponse("error", "An error occurred: " + e.getMessage());
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }

        return new ResponseEntity<>(response, status);
    }

    @DeleteMapping("/{fname}")
    public ResponseEntity<StandardResponse> deleteEmployeeByFname(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String fname) {

        log.info("Received request to delete employees with fname: {}", fname);
        StandardResponse response;
        HttpStatus status;

        if (!validateBasicAuth(authHeader)) {
            log.warn("Unauthorized access attempt");
            response = new StandardResponse("error", "Invalid credentials");
            status = HttpStatus.UNAUTHORIZED;
        } else {
            try {
                employeeService.deleteEmployeeByFname(fname);
                log.info("Employees with fname '{}' deleted successfully", fname);
                response = new StandardResponse("success", "Employees with fname '" + fname + "' deleted successfully");
                status = HttpStatus.OK;
            } catch (EmployeeNotFoundException e) {
                log.info("EmployeeNotFoundException: {}", e.getMessage());
                response = new StandardResponse("error", e.getMessage());
                status = HttpStatus.NOT_FOUND;
            } catch (Exception e) {
                log.error("Error occurred while deleting employees with fname: {}", fname, e);
                response = new StandardResponse("error", "An error occurred: " + e.getMessage());
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }

        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/export-to-pdf")
    public void generatePdfFile(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD:HH:MM:SS");
        String currentDateTime = dateFormat.format(new Date());
        
        String headerkey = "Content-Disposition";
        String headervalue = "attachment; filename=Employee" + currentDateTime + ".pdf";
        
        response.setHeader(headerkey, headervalue);
        List<Employee> listofEmp = employeeService.getAllExportEmployees();
        PdfGenerator generator = new PdfGenerator();
        generator.generate(listofEmp, response);
    }

    @RequestMapping(path = "/export-to-excel", method = RequestMethod.GET)
    public ResponseEntity<Object> exportToExcel(HttpServletResponse response) {
        try {
            List<Employee> listEmployee = employeeService.getAllExcelEmployees();
            EmployeeExcelExporter excelExporter = new EmployeeExcelExporter(listEmployee);
            excelExporter.export(response);
            log.info("Exported JSON Data into Excel File ");
            return OutputResponse.getResponse("Data exported successfully", HttpStatus.OK);
        } catch (IOException e) {   
            log.error("Error Exporting Data into Excel Format");
            return OutputResponse.getResponse("Failed to export data to Excel: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Error Exporting Data into Excel Format");
            return OutputResponse.getResponse("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

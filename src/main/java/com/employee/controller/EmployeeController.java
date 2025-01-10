package com.employee.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.employee.EmployeeManagementApplication;
import com.employee.entity.Employee;
import com.employee.exception.EmployeeNotFoundException;
import com.employee.repository.EmployeeRepository;
import com.employee.response.OutputResponse;
import com.employee.response.StandardResponse;
//import com.employee.service.PdfGenerator;
//import com.lowagie.text.DocumentException;
import com.employee.service.EmployeeExcelExporter;
//import com.employee.service.EmployeePdfExporter;
//import com.employee.service.EmployeePdfExporter;
import com.employee.service.EmployeeService;
import com.employee.service.PdfGenerator;
import com.lowagie.text.DocumentException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/api")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private  EmployeeRepository employeeRepository;

	
	private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);



	private static final RequestMethod[] GET = null;


	private String userName = "dipali";
    private String passWord = "1234";
	
    
    
    @PostMapping("/employees")
    public ResponseEntity<StandardResponse> addEmployee(@RequestHeader("username") String username,@RequestHeader("password") String password, @RequestBody Employee employee) {
        StandardResponse response;
        HttpStatus status;

        if (!userName.equals(username) || !passWord.equals(password)) {
            log.warn("Unauthorized access attempt: Username - {}", username);
            response = new StandardResponse("error", "Invalid credentials");
            status = HttpStatus.UNAUTHORIZED;
        } else {
            try {
                response = employeeService.addEmployee(employee);
                status = response.getStatus().equals("success") ? HttpStatus.OK : HttpStatus.OK;
            } catch (DataAccessException e) {
            	log.info("Failed to perform database operation. Please try again later");
                response = new StandardResponse("error", "Failed to perform database operation. Please try again later.");
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            } catch (Exception e) {
                response = new StandardResponse("error", "An unexpected error occurred. Please try again later.");
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
           
        }
        return new ResponseEntity<>(response, status);
    }
    
    
		 @GetMapping("")
		    public ResponseEntity<?> getEmployeeIdAndName() {
		        ResponseEntity<?> response = employeeService.getEmployeeIdAndFname();
		        return response;
		    }
		
		 
		
          //Retrive xml data
		 @GetMapping(value = "/xml",produces = { "application/xml" })
		    public ResponseEntity<?> getEmployeeIdName() {
		        ResponseEntity<?> response = employeeService.getEmployeeIdName();
		        return response;
		    }
		 
		 
		
	  
		
		 
		 
		 @GetMapping("/{empid}")
		    public ResponseEntity<?> getEmployeeByEmpId(@RequestHeader("username") String username,@RequestHeader("password") String password, @PathVariable String empid) {
		        log.info("Received request to get employee with empid: {}", empid);
		        Object response;
		        HttpStatus status;

		        if (!userName.equals(username) || !passWord.equals(password)) {
		            log.warn("Unauthorized access attempt: Username - {}", username);
		            response = new StandardResponse("error", "Invalid credentials");
		            status = HttpStatus.UNAUTHORIZED;
		        } else {
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
		        }

		        return new ResponseEntity<>(response, status);
		    }
		
		 	 
		 
		 @PutMapping("/{empid}")
		    public ResponseEntity<?> updateEmployeeDetails(@RequestHeader("username") String username,@RequestHeader("password") String password, @PathVariable String empid,
		                          @RequestBody Employee updatedEmployee) {
		        log.info("Received request to update employee with empid: {}", empid);
		        StandardResponse response;
		        HttpStatus status;

		        if (!userName.equals(username) || !passWord.equals(password)) {
		            log.warn("Unauthorized access attempt: Username - {}", username);
		            response = new StandardResponse("error", "Invalid credentials");
		            status = HttpStatus.UNAUTHORIZED;
		        } else {
		            try {
		                employeeService.updateEmployeeDetails(empid, updatedEmployee);
		                log.info("Employee details updated successfully for empid: {}", empid);
		                response = new StandardResponse("success", "Employee details updated successfully");
		                status = HttpStatus.OK;
		            } catch (EmployeeNotFoundException e) {
		                log.info("EmployeeNotFoundException: {}", e.getMessage());
		                response = new StandardResponse("error", e.getMessage());
		                status = HttpStatus.NOT_FOUND;
		            } catch (Exception e) {
		                log.error("Error occurred while updating employee with empid: {}", empid, e);
		                response = new StandardResponse("error", "An error occurred: " + e.getMessage());
		                status = HttpStatus.INTERNAL_SERVER_ERROR;
		            }
		        }

		        return new ResponseEntity<>(response, status);
		    }
		
		 
		 
		 
		 
			/*
			 * @DeleteMapping("{fname}") public ResponseEntity<String>
			 * deleteEmployeeByFname(@PathVariable String fname) { try {
			 * employeeService.deleteEmployeeByFname(fname); return new
			 * ResponseEntity<>("Employees with fname '" + fname + "' deleted successfully",
			 * HttpStatus.OK); } catch (Exception e) { return new
			 * ResponseEntity<>("Failed to delete employees with fname '" + fname + "': " +
			 * e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); } }
			 */
	 	 
		
		 
		 @DeleteMapping("{fname}")
		    public ResponseEntity<StandardResponse> deleteEmployeeByFname(@RequestHeader("username") String username,@RequestHeader("password") String password,@PathVariable String fname) {

		        log.info("Received request to delete employees with fname: {}", fname);
		        StandardResponse response;
		        HttpStatus status;

		        if (!userName.equals(username) || !passWord.equals(password)) {
		            log.warn("Unauthorized access attempt: Username - {}", username);
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
		
	 
	 
	 
			
			 
		 
		 
		 
			/*
			 * @GetMapping("/v3") // Map GET requests to the generatePDF method public
			 * ResponseEntity<String> generatePDF(@RequestHeader Map<String, String>
			 * headers, HttpServletResponse response) {
			 * log.debug("Received generatePDF request"); // Log the request
			 * 
			 * 
			 * 
			 * try { List<Employee> employees = employeeService.getAllExportEmployees(); //
			 * Get all employees
			 * 
			 * response.setContentType("application/pdf"); // Set response content type
			 * String headerKey = "Content-Disposition"; // Define header key for content
			 * disposition String headerValue = "attachment; filename=employees.pdf"; //
			 * Define header value for file attachment response.setHeader(headerKey,
			 * headerValue); // Set the header
			 * 
			 * employeeService.generate(employees, response); // Generate the PDF
			 * 
			 * return new ResponseEntity<>("PDF Generated Successfully", HttpStatus.OK); //
			 * Return success response } catch (DocumentException | IOException e) { //
			 * Catch exceptions logger.error("Error generating PDF", e); // Log the error
			 * return new
			 * ResponseEntity<>(StatusMessageUtil.getStatusMessage("pdf_generate_error"),
			 * HttpStatus.INTERNAL_SERVER_ERROR); // Return error response } }
			 */
			/*
			 * @RequestMapping(path = "/export-to-pdf", method = RequestMethod.GET) public
			 * ResponseEntity<Object> exportToPDF(HttpServletResponse response) { try {
			 * List<Employee> listEmployees = employeeService.getAllExportEmployees();
			 * PdfGenerator exporter = new PdfGenerator(listEmployees);
			 * exporter.export(response); log.info("Exported JSON Data into PDF File");
			 * return OutputResponse.getResponse("Data exported successfully",
			 * HttpStatus.OK); } catch (IOException e) {
			 * log.error("Error Exporting Data into PDF Format", e); return
			 * OutputResponse.getResponse("Failed to export data to PDF: " + e.getMessage(),
			 * HttpStatus.INTERNAL_SERVER_ERROR); } catch (Exception e) {
			 * log.error("Error Exporting Data into PDF Format", e); return
			 * OutputResponse.getResponse("An error occurred: " + e.getMessage(),
			 * HttpStatus.INTERNAL_SERVER_ERROR); } }
			 */
		 
		 
		 
		 @GetMapping("/export-to-pdf")
			public void generatePdfFile(HttpServletResponse response) throws DocumentException, IOException 
			{
			 // setting up your response type - 1
			 response.setContentType("application/pdf");
			 
			  DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD:HH:MM:SS");
			  String currentDateTime = dateFormat.format(new Date());
			 
			  String headerkey = "Content-Disposition";
			  String headervalue = "attachment; filename=Employee" + currentDateTime + ".pdf";
			  
			  response.setHeader(headerkey, headervalue);
			// reading all the employee
			  List <Employee> listofEmp = employeeService.getAllExportEmployees();
			  // object of pdf generator
			  PdfGenerator generator = new PdfGenerator();
			  //passing list of employee and response object
			  generator.generate(listofEmp, response);
			}

		 
		 
		 
	 
	 @RequestMapping(path="/export-to-excel", method = RequestMethod.GET)   // to get data in excel format
	    public ResponseEntity<Object> exportToExcel(HttpServletResponse response) {
	        try {
	            List<Employee> listEmployee = employeeService.getAllExcelEmployees();;
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

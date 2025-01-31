package com.employee.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmployeeNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 7428051251365675318L;

	   public EmployeeNotFoundException(String message) {
	      super(message);
	   }

}

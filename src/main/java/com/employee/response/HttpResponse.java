package com.employee.response;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HttpResponse {

	//generateResponse method
	public static ResponseEntity<Object> generateResponse(String message, HttpStatus status_code, Object responseObj) {
        Map<String, Object> map = new HashMap<String, Object>();
            map.put("status_message", message);
            map.put("status_code", status_code.value());
            map.put("data", responseObj);

            return new ResponseEntity<Object>(map,status_code);
    }
}
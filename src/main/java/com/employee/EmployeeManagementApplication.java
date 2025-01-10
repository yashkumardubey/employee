package com.employee;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class EmployeeManagementApplication extends SpringBootServletInitializer {
    
    private static final Logger logger = LogManager.getLogger(EmployeeManagementApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementApplication.class, args);
        logger.info("Employee Management Application started successfully");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(EmployeeManagementApplication.class);
    }
}


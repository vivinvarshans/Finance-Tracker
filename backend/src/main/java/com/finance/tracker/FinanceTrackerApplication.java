package com.finance.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for Finance Tracker API
 * 
 * This Spring Boot application implements a RESTful API with the following architecture:
 * - MVC Pattern with clear separation of concerns
 * - Three core modules: Authentication, Transaction Management, and Budget Analysis
 * - Spring Data JPA for ORM and simplified database operations
 * - Proper exception handling and validation at the service layer
 * - RESTful endpoints for CRUD operations (GET, POST, PUT, DELETE)
 * 
 * @author Finance Tracker Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class FinanceTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceTrackerApplication.class, args);
        System.out.println("=================================================");
        System.out.println("Finance Tracker API is running successfully!");
        System.out.println("API Base URL: http://localhost:8080/api");
        System.out.println("=================================================");
    }
}

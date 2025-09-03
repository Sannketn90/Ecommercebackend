package com.project.ecommerce;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class EcommercebackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommercebackendApplication.class, args);
		log.info("Hello, Ecommerce Backend Application has started successfully!");
		log.info("Visit the API documentation at: http://localhost:8080/swagger-ui/index.html");
	}

}

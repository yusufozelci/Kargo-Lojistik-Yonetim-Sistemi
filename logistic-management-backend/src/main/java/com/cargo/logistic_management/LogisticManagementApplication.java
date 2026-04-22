package com.cargo.logistic_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LogisticManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogisticManagementApplication.class, args);
    }
}
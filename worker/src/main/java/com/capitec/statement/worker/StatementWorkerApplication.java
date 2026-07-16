package com.capitec.statement.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.capitec.statement.worker"})
@EntityScan(basePackages = "com.capitec.statement.domain.entity")
@EnableJpaRepositories(basePackages = "com.capitec.statement.domain.repository")
public class StatementWorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(StatementWorkerApplication.class, args);
    }
}
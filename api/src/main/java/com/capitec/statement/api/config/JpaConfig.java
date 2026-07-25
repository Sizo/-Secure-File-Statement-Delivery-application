package com.capitec.statement.api.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "com.capitec.statement.domain.entity")
@EnableJpaRepositories(basePackages = "com.capitec.statement.domain.repository")
public class JpaConfig {
}

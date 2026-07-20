package com.capitec.statement.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // Virtual thread execution for async methods is configured via application.yml (spring.threads.virtual.enabled)
}

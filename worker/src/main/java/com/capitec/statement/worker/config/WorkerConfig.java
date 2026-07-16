package com.capitec.statement.worker.config;

import org.springframework.context.annotation.Configuration;

/**
 * Worker Module Configuration.
 * <p>
 * HikariCP restriction rationale:
 * The maximum pool size is set to 3 to strictly limit the number of concurrent database connections
 * this worker can acquire. Since this is an asynchronous worker processing statements from a queue,
 * a small, fixed connection pool ensures it doesn't overwhelm the database during traffic bursts.
 * Virtual threads are enabled, meaning thread contention is low, but we still must enforce database
 * resource constraints. Configured via application.yml (maximum-pool-size: 3).
 */
@Configuration
public class WorkerConfig {
}
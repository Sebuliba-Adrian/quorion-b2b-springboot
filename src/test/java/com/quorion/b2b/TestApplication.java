package com.quorion.b2b;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Test Application - Excludes legacy controllers and legacy model entities
 * 
 * We're testing hexagonal architecture endpoints, so we exclude:
 * - Legacy controllers (depend on legacy services)
 * - Legacy model entities (have conflicting FK relationships)
 */
@SpringBootApplication(scanBasePackages = {
    "com.quorion.b2b.adapter",
    "com.quorion.b2b.domain",
    "com.quorion.b2b.config",
    "com.quorion.b2b.security"
})
@EntityScan(basePackages = {
    "com.quorion.b2b.adapter.output.entity"
})
@EnableJpaRepositories(basePackages = {
    "com.quorion.b2b.adapter.output.repository"
})
public class TestApplication {
    // Test-specific application class that:
    // - Excludes legacy controllers
    // - Only scans hexagonal entities (adapter.output.entity)
    // - Only scans hexagonal repositories (adapter.output.repository)
    // Hexagonal architecture controllers are in adapter.input.rest package
}


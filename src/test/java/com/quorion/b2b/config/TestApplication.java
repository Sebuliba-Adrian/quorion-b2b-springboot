package com.quorion.b2b.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Test Application - Excludes Legacy Controllers
 * 
 * This test-specific application configuration excludes legacy controllers
 * that expect legacy service types, allowing tests to run during the
 * architectural transition period.
 */
@SpringBootApplication(scanBasePackages = "com.quorion.b2b")
@ComponentScan(
    basePackages = "com.quorion.b2b",
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = "com\\.quorion\\.b2b\\.controller\\..*"
        )
    }
)
@EnableJpaRepositories(basePackages = "com.quorion.b2b")
@EntityScan(basePackages = "com.quorion.b2b")
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}


package com.quorion.b2b.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Test Configuration that excludes legacy controllers
 */
@TestConfiguration
@EnableAutoConfiguration
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
public class TestExcludeConfig {
    // Configuration to exclude legacy controllers
}



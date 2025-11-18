package com.quorion.b2b.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Test Application Configuration
 * 
 * Excludes legacy service classes from component scanning in tests
 * This ensures our test stubs are used instead
 */
@TestConfiguration
@Profile("test")
@EnableAutoConfiguration
@ComponentScan(
    basePackages = "com.quorion.b2b",
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = "com\\.quorion\\.b2b\\.service\\..*Service"
        )
    }
)
@Import(ComprehensiveLegacyServiceStubs.class)
public class TestApplicationConfiguration {
    // This configuration ensures legacy services are excluded from component scanning
    // and our test stubs are used instead
}



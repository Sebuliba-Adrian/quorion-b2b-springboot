package com.quorion.b2b.config;

import com.quorion.b2b.repository.CustomerRepository;
import com.quorion.b2b.service.CustomerService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Provides stub implementations of legacy services for testing
 * This allows legacy controllers to be instantiated during tests
 * without requiring full legacy service implementations
 */
@TestConfiguration
public class LegacyServiceStubs {

    /**
     * Stub CustomerService for legacy controllers
     * This allows legacy controllers to be instantiated during tests
     */
    @Bean
    @Primary
    public CustomerService customerService() {
        // Create a mock repository
        CustomerRepository mockRepo = Mockito.mock(CustomerRepository.class);
        
        return new CustomerService(mockRepo);
    }
}


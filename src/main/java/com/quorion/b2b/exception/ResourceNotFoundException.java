package com.quorion.b2b.exception;

import java.util.UUID;

/**
 * Exception thrown when a requested resource is not found
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, UUID id) {
        super(String.format("%s not found with id: %s", resourceName, id));
    }

    public ResourceNotFoundException(String resourceName, String field, String value) {
        super(String.format("%s not found with %s: %s", resourceName, field, value));
    }
}

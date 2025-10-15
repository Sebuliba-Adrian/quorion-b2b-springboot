package com.quorion.b2b.controller;

import com.quorion.b2b.model.commerce.Notification;
import jakarta.validation.Valid;
import com.quorion.b2b.repository.NotificationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
/**
 * Notification Controller
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "Notification management")
public class NotificationController {
    private final NotificationRepository notificationRepository;
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all notifications")
    public ResponseEntity<List<Notification>> getAll() {
        return ResponseEntity.ok(notificationRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID")
    public ResponseEntity<Notification> getById(@PathVariable UUID id) {
        return notificationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create notification")
    public ResponseEntity<Notification> create(@Valid @RequestBody Notification notification) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationRepository.save(notification));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update notification")
    public ResponseEntity<Notification> update(@PathVariable UUID id, @RequestBody Notification details) {
        return notificationRepository.findById(id)
                .map(existing -> {
                    details.setId(id);
                    return ResponseEntity.ok(notificationRepository.save(details));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete notification")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (notificationRepository.existsById(id)) {
            notificationRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}


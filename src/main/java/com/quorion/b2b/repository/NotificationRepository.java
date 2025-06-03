package com.quorion.b2b.repository;

import com.quorion.b2b.model.commerce.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByRecipientId(UUID recipientId);
    List<Notification> findByRecipientIdAndIsReadFalse(UUID recipientId);
}

package com.quorion.b2b.model.commerce;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import com.quorion.b2b.model.tenant.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;

/**
 * User notifications
 */
@Entity
@Table(name = "notification")
@Data
@EqualsAndHashCode(callSuper = true, exclude = "recipient")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Tenant recipient;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private NotificationType notificationType;

    @NotBlank
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank
    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "link", length = 500)
    private String link;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> data = new HashMap<>();

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @Column(name = "is_email_sent", nullable = false)
    @Builder.Default
    private Boolean isEmailSent = false;

    @Column(name = "is_sms_sent", nullable = false)
    @Builder.Default
    private Boolean isSmsSent = false;

    /**
     * Mark notification as read
     */
    public void markAsRead() {
        this.isRead = true;
    }
}

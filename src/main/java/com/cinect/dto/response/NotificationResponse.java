package com.cinect.dto.response;

import com.cinect.entity.enums.NotificationType;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationResponse {
    private UUID id;
    private String title;
    private String message;
    private NotificationType type;
    private Boolean isRead;
    private String link;
    private Instant createdAt;
}

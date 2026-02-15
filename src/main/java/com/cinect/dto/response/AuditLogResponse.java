package com.cinect.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLogResponse {
    private UUID id;
    private UUID userId;
    private String userEmail;
    private String action;
    private String entityType;
    private String entityId;
    private Map<String, Object> oldValues;
    private Map<String, Object> newValues;
    private String ipAddress;
    private String userAgent;
    private Instant createdAt;
}

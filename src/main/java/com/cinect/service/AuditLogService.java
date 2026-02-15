package com.cinect.service;

import com.cinect.entity.AuditLog;
import com.cinect.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(UUID userId, String userEmail, String action, String entityType, String entityId,
                    Map<String, Object> oldValues, Map<String, Object> newValues, HttpServletRequest request) {
        var log = AuditLog.builder()
                .userId(userId)
                .userEmail(userEmail)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .oldValues(oldValues)
                .newValues(newValues)
                .ipAddress(request != null ? getClientIp(request) : null)
                .userAgent(request != null ? request.getHeader("User-Agent") : null)
                .build();
        auditLogRepository.save(log);
    }

    public Page<AuditLog> findAll(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Page<AuditLog> findByEntityType(String entityType, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return auditLogRepository.findByEntityType(entityType, pageable);
    }

    private String getClientIp(HttpServletRequest request) {
        var xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

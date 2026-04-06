package com.cinect.service;

import com.cinect.entity.AuditLog;
import com.cinect.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
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

    public Page<AuditLog> findFiltered(
            String entityType,
            String search,
            String from,
            String to,
            String action,
            int page,
            int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Specification<AuditLog> spec = (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            if (entityType != null && !entityType.isBlank()) {
                predicates.add(cb.equal(root.get("entityType"), entityType));
            }
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("userEmail")), pattern),
                        cb.like(cb.lower(root.get("action")), pattern)
                ));
            }
            if (from != null && !from.isBlank()) {
                LocalDate fromDate = LocalDate.parse(from);
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        fromDate.atStartOfDay(ZoneOffset.UTC).toInstant()
                ));
            }
            if (to != null && !to.isBlank()) {
                LocalDate toDate = LocalDate.parse(to);
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("createdAt"),
                        toDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant()
                ));
            }
            if (action != null && !action.isBlank()) {
                String prefix;
                String normalized = action.toUpperCase();
                if ("CREATE".equals(normalized)) prefix = "POST";
                else if ("UPDATE".equals(normalized)) prefix = "PUT";
                else if ("DELETE".equals(normalized)) prefix = "DELETE";
                else prefix = normalized;
                predicates.add(cb.like(cb.upper(root.get("action")), prefix + "%"));
            }
            query.orderBy(cb.desc(root.get("createdAt")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return auditLogRepository.findAll(spec, pageable);
    }

    private String getClientIp(HttpServletRequest request) {
        var xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

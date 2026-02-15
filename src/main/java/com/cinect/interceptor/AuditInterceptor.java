package com.cinect.interceptor;

import com.cinect.security.UserPrincipal;
import com.cinect.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuditInterceptor implements HandlerInterceptor {

    private final AuditLogService auditLogService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        var method = request.getMethod();
        if (!("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method) || "DELETE".equals(method))) {
            return;
        }
        UUID userId = null;
        String userEmail = null;
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            userId = principal.getId();
            userEmail = principal.getEmail();
        }
        var path = request.getRequestURI();
        var entityType = inferEntityType(path);
        var entityId = inferEntityId(path);
        try {
            auditLogService.log(userId, userEmail, method, entityType, entityId, null, null, request);
        } catch (Exception ignored) {
        }
    }

    private String inferEntityType(String path) {
        if (path.contains("/movies")) return "Movie";
        if (path.contains("/cinemas")) return "Cinema";
        if (path.contains("/rooms")) return "Room";
        if (path.contains("/showtimes")) return "Showtime";
        if (path.contains("/bookings")) return "Booking";
        if (path.contains("/promotions")) return "Promotion";
        if (path.contains("/pricing-rules")) return "PricingRule";
        return "Unknown";
    }

    private String inferEntityId(String path) {
        var parts = path.split("/");
        for (int i = 0; i < parts.length - 1; i++) {
            if (i + 1 < parts.length) {
                var next = parts[i + 1];
                if (!next.isEmpty() && next.matches("[0-9a-fA-F-]{36}")) {
                    return next;
                }
            }
        }
        return null;
    }
}

package com.cinect.service;

import com.cinect.dto.response.NotificationResponse;
import com.cinect.entity.Notification;
import com.cinect.entity.enums.NotificationType;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.repository.NotificationRepository;
import com.cinect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public Page<NotificationResponse> getByUser(UUID userId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable).map(this::toResponse);
    }

    @Transactional
    public void markRead(UUID id, UUID userId) {
        var n = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!n.getUser().getId().equals(userId)) return;
        n.setIsRead(true);
        notificationRepository.save(n);
    }

    @Transactional
    public void markAllRead(UUID userId) {
        notificationRepository.markAllAsRead(userId);
    }

    @Transactional
    public void createNotification(UUID userId, String title, String message, NotificationType type, String link) {
        var user = userRepository.findById(userId).orElse(null);
        if (user == null) return;
        var n = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type != null ? type : NotificationType.SYSTEM)
                .link(link)
                .build();
        notificationRepository.save(n);
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .isRead(n.getIsRead())
                .link(n.getLink())
                .createdAt(n.getCreatedAt())
                .build();
    }
}

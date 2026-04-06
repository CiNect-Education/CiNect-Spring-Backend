package com.cinect.service;

import com.cinect.dto.request.UpdateProfileRequest;
import com.cinect.dto.response.UserResponse;
import com.cinect.entity.User;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateProfileRequest req) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (req.getFullName() != null) user.setFullName(normalize(req.getFullName()));
        if (req.getPhone() != null) user.setPhone(normalize(req.getPhone()));
        if (req.getAvatar() != null) user.setAvatar(normalize(req.getAvatar()));
        if (req.getDateOfBirth() != null) user.setDateOfBirth(req.getDateOfBirth());
        if (req.getGender() != null) user.setGender(normalize(req.getGender()));
        if (req.getCity() != null) user.setCity(normalize(req.getCity()));
        user = userRepository.save(user);
        return toResponse(user);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private UserResponse toResponse(User user) {
        String role = user.getRoles().isEmpty() ? "USER"
                : user.getRoles().iterator().next().getName().name();
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .role(role)
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .city(user.getCity())
                .isActive(user.getIsActive())
                .emailVerified(user.getEmailVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

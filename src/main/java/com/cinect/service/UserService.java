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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateProfileRequest req) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getPhone() != null) user.setPhone(req.getPhone());
        if (req.getAvatar() != null) user.setAvatar(req.getAvatar());
        if (req.getDateOfBirth() != null) user.setDateOfBirth(req.getDateOfBirth());
        if (req.getGender() != null) user.setGender(req.getGender());
        if (req.getCity() != null) user.setCity(req.getCity());
        user = userRepository.save(user);
        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .city(user.getCity())
                .isActive(user.getIsActive())
                .emailVerified(user.getEmailVerified())
                .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .build();
    }
}

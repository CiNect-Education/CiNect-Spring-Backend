package com.cinect.service;

import com.cinect.dto.request.*;
import com.cinect.dto.response.AuthResponse;
import com.cinect.dto.response.UserResponse;
import com.cinect.entity.Membership;
import com.cinect.entity.MembershipTier;
import com.cinect.entity.User;
import com.cinect.entity.enums.UserRole;
import com.cinect.exception.BadRequestException;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.exception.UnauthorizedException;
import com.cinect.repository.MembershipRepository;
import com.cinect.repository.MembershipTierRepository;
import com.cinect.repository.RoleRepository;
import com.cinect.repository.UserRepository;
import com.cinect.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Base64;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipTierRepository membershipTierRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        var userRole = roleRepository.findByName(UserRole.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role USER not found"));
        var bronzeTier = membershipTierRepository.findByName("Bronze")
                .orElse(membershipTierRepository.findAll().stream()
                        .min((a, b) -> Integer.compare(a.getLevel(), b.getLevel()))
                        .orElseThrow(() -> new ResourceNotFoundException("No membership tier found")));

        var user = User.builder()
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .phone(req.getPhone())
                .isActive(true)
                .emailVerified(false)
                .roles(new HashSet<>(Set.of(userRole)))
                .build();
        user = userRepository.save(user);

        var membership = Membership.builder()
                .user(user)
                .tier(bronzeTier)
                .currentPoints(0)
                .totalPoints(0)
                .memberSince(Instant.now())
                .build();
        membershipRepository.save(membership);

        var accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), "USER");
        var refreshToken = jwtService.generateRefreshToken(user.getId());
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(toUserResponse(user))
                .build();
    }

    public AuthResponse login(LoginRequest req) {
        var user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));
        if (!user.getIsActive()) {
            throw new BadRequestException("Account is deactivated");
        }
        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            throw new BadRequestException("This account uses social login. Please sign in with your social provider.");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        var role = user.getRoles().isEmpty() ? "USER" : user.getRoles().iterator().next().getName().name();
        var accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), role);
        var refreshToken = jwtService.generateRefreshToken(user.getId());
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(toUserResponse(user))
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest req) {
        if (!jwtService.isTokenValid(req.getRefreshToken())) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }
        var userId = jwtService.getUserIdFromToken(req.getRefreshToken());
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        if (!req.getRefreshToken().equals(user.getRefreshToken())) {
            throw new UnauthorizedException("Refresh token mismatch");
        }
        var role = user.getRoles().isEmpty() ? "USER" : user.getRoles().iterator().next().getName().name();
        var accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), role);
        var refreshToken = jwtService.generateRefreshToken(user.getId());
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(toUserResponse(user))
                .build();
    }

    public UserResponse me(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toUserResponse(user);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest req) {
        var user = userRepository.findByEmail(req.getEmail()).orElse(null);
        if (user != null) {
            var token = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
            user.setResetToken(token);
            user.setResetTokenExp(Instant.now().plusSeconds(3600)); // 1 hour
            userRepository.save(user);
            // TODO: Send email with reset link containing token
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest req) {
        var user = userRepository.findByResetToken(req.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));
        if (user.getResetTokenExp() == null || user.getResetTokenExp().isBefore(Instant.now())) {
            throw new BadRequestException("Reset token has expired");
        }
        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExp(null);
        userRepository.save(user);
    }

    @Transactional
    public void logout(UUID userId) {
        userRepository.findById(userId).ifPresent(u -> {
            u.setRefreshToken(null);
            userRepository.save(u);
        });
    }

    @Transactional
    public AuthResponse findOrCreateOAuthUser(Map<String, Object> profile) {
        String provider = (String) profile.get("provider");
        String providerId = (String) profile.get("providerId");
        String email = (String) profile.get("email");
        String fullName = (String) profile.get("fullName");
        String avatar = (String) profile.get("avatar");

        // 1. Check by provider + providerId
        var userOpt = userRepository.findByProviderAndProviderId(provider, providerId);
        User user;

        if (userOpt.isPresent()) {
            user = userOpt.get();
        } else if (email != null && !email.isBlank()) {
            // 2. Check by email — link provider
            var emailUserOpt = userRepository.findByEmail(email.toLowerCase());
            if (emailUserOpt.isPresent()) {
                user = emailUserOpt.get();
                user.setProvider(provider);
                user.setProviderId(providerId);
                if (user.getAvatar() == null || user.getAvatar().isBlank()) {
                    user.setAvatar(avatar);
                }
                user = userRepository.save(user);
            } else {
                // 3. Create new user
                user = createOAuthUser(provider, providerId, email, fullName, avatar);
            }
        } else {
            // 3. Create with synthetic email
            String syntheticEmail = provider.toLowerCase() + "_" + providerId + "@oauth.local";
            user = createOAuthUser(provider, providerId, syntheticEmail, fullName, avatar);
        }

        var role = user.getRoles().isEmpty() ? "USER" : user.getRoles().iterator().next().getName().name();
        var accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), role);
        var refreshToken = jwtService.generateRefreshToken(user.getId());
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(toUserResponse(user))
                .build();
    }

    private User createOAuthUser(String provider, String providerId, String email, String fullName, String avatar) {
        var userRole = roleRepository.findByName(com.cinect.entity.enums.UserRole.USER)
                .orElse(null);
        var bronzeTier = membershipTierRepository.findByName("Bronze")
                .orElse(null);

        var user = User.builder()
                .email(email.toLowerCase())
                .fullName(fullName != null && !fullName.isBlank() ? fullName : "User")
                .avatar(avatar)
                .provider(provider)
                .providerId(providerId)
                .isActive(true)
                .emailVerified(true)
                .roles(userRole != null ? new HashSet<>(Set.of(userRole)) : new HashSet<>())
                .build();
        user = userRepository.save(user);

        if (bronzeTier != null) {
            var membership = Membership.builder()
                    .user(user)
                    .tier(bronzeTier)
                    .currentPoints(0)
                    .totalPoints(0)
                    .memberSince(Instant.now())
                    .build();
            membershipRepository.save(membership);
        }

        return user;
    }

    private UserResponse toUserResponse(User user) {
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

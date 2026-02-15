package com.cinect.controller;

import com.cinect.dto.request.LoginRequest;
import com.cinect.dto.request.RefreshTokenRequest;
import com.cinect.dto.request.RegisterRequest;
import com.cinect.dto.request.ForgotPasswordRequest;
import com.cinect.dto.request.ResetPasswordRequest;
import com.cinect.dto.request.UpdateProfileRequest;
import com.cinect.dto.response.ApiResponse;
import com.cinect.dto.response.AuthResponse;
import com.cinect.dto.response.UserResponse;
import com.cinect.security.UserPrincipal;
import com.cinect.service.AuthService;
import com.cinect.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest req) {
        var data = authService.register(req);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest req) {
        var data = authService.login(req);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest req) {
        var data = authService.refreshToken(req);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(@AuthenticationPrincipal UserPrincipal principal) {
        var data = authService.me(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest req) {
        authService.forgotPassword(req);
        return ResponseEntity.ok(ApiResponse.success(null, "If the email exists, a reset link has been sent"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        authService.resetPassword(req);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserPrincipal principal) {
        authService.logout(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @RequestBody UpdateProfileRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        var data = userService.updateProfile(principal.getId(), req);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}

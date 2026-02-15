package com.cinect.security;

import com.cinect.entity.User;
import lombok.*;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class UserPrincipal {
    private UUID id;
    private String email;
    private String fullName;
    private Set<String> roles;

    public static UserPrincipal from(User user) {
        return UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream()
                        .map(r -> r.getName().name())
                        .collect(Collectors.toSet()))
                .build();
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }
}

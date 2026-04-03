package com.cinect.util;

import com.cinect.entity.Role;
import com.cinect.entity.enums.UserRole;

import java.util.Set;

/**
 * Matches Nest {@code pickPrimaryRole}: ADMIN &gt; STAFF &gt; USER.
 */
public final class RoleUtil {

    private RoleUtil() {
    }

    public static String pickPrimaryRoleName(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return UserRole.USER.name();
        }
        if (roles.stream().anyMatch(r -> r.getName() == UserRole.ADMIN)) {
            return UserRole.ADMIN.name();
        }
        if (roles.stream().anyMatch(r -> r.getName() == UserRole.STAFF)) {
            return UserRole.STAFF.name();
        }
        return UserRole.USER.name();
    }
}

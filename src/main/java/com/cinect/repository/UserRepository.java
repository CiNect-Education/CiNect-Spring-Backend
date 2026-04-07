package com.cinect.repository;

import com.cinect.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByResetToken(String resetToken);
    Optional<User> findByProviderAndProviderId(String provider, String providerId);

    /**
     * Text search only — never pass null (caller uses {@link #findAll(Pageable)} instead).
     * Avoids PostgreSQL "could not determine data type" / lower(bytea) from nullable params in one query.
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<User> searchUsersByTerm(@Param("search") String search, Pageable pageable);
}

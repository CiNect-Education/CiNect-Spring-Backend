package com.cinect.repository;

import com.cinect.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, UUID> {
    Optional<Membership> findByUserId(UUID userId);

    @Query("""
            SELECT m
            FROM Membership m
            JOIN FETCH m.user u
            JOIN FETCH m.tier t
            WHERE u.id = :userId
            """)
    Optional<Membership> findProfileByUserId(@Param("userId") UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT m
            FROM Membership m
            JOIN FETCH m.user u
            JOIN FETCH m.tier t
            WHERE u.id = :userId
            """)
    Optional<Membership> findProfileByUserIdForUpdate(@Param("userId") UUID userId);
}

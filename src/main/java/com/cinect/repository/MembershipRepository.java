package com.cinect.repository;

import com.cinect.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, UUID> {
    Optional<Membership> findByUserId(UUID userId);
}

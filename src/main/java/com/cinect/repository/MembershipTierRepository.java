package com.cinect.repository;

import com.cinect.entity.MembershipTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembershipTierRepository extends JpaRepository<MembershipTier, UUID> {
    Optional<MembershipTier> findByName(String name);
    Optional<MembershipTier> findByLevel(Integer level);
    Optional<MembershipTier> findFirstByPointsRequiredLessThanEqualOrderByLevelDesc(Integer points);
}

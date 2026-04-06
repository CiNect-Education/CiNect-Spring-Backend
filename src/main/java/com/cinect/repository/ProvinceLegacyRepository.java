package com.cinect.repository;

import com.cinect.entity.ProvinceLegacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProvinceLegacyRepository extends JpaRepository<ProvinceLegacy, UUID> {
    @Query("SELECT l FROM ProvinceLegacy l JOIN FETCH l.provinceNew ORDER BY l.code")
    List<ProvinceLegacy> findAllOrderedWithNew();
}

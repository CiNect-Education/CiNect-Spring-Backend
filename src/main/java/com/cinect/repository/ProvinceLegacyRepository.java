package com.cinect.repository;

import com.cinect.entity.ProvinceLegacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProvinceLegacyRepository extends JpaRepository<ProvinceLegacy, UUID> {
    @Query("SELECT l FROM ProvinceLegacy l JOIN FETCH l.provinceNew ORDER BY l.code")
    List<ProvinceLegacy> findAllOrderedWithNew();

    @Query("SELECT l FROM ProvinceLegacy l JOIN FETCH l.provinceNew WHERE l.code = :code")
    Optional<ProvinceLegacy> findByCodeWithNew(@Param("code") String code);

    @Query("SELECT l FROM ProvinceLegacy l JOIN FETCH l.provinceNew " +
            "WHERE LOWER(l.nameVi) = LOWER(:name) OR LOWER(l.nameEn) = LOWER(:name)")
    Optional<ProvinceLegacy> findByNameWithNew(@Param("name") String name);
}

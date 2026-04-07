package com.cinect.repository;

import com.cinect.entity.ProvinceNew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProvinceNewRepository extends JpaRepository<ProvinceNew, UUID> {
    Optional<ProvinceNew> findByCode(String code);

    List<ProvinceNew> findAllByOrderBySortOrderAsc();

    @Query("SELECT p FROM ProvinceNew p WHERE LOWER(p.nameVi) = LOWER(:name) OR LOWER(p.nameEn) = LOWER(:name)")
    Optional<ProvinceNew> findByName(@Param("name") String name);
}

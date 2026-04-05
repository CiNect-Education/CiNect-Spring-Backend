package com.cinect.repository;

import com.cinect.entity.ProvinceNew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProvinceNewRepository extends JpaRepository<ProvinceNew, UUID> {
    Optional<ProvinceNew> findByCode(String code);

    List<ProvinceNew> findAllByOrderBySortOrderAsc();
}

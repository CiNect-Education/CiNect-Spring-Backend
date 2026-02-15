package com.cinect.repository;

import com.cinect.entity.Snack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SnackRepository extends JpaRepository<Snack, UUID> {
    List<Snack> findByCinema_IdAndIsActiveTrue(UUID cinemaId);
    List<Snack> findByIsActiveTrue();
}

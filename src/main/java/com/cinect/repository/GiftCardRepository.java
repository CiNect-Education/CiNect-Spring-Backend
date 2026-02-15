package com.cinect.repository;

import com.cinect.entity.GiftCard;
import com.cinect.entity.enums.GiftCardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GiftCardRepository extends JpaRepository<GiftCard, UUID> {
    Optional<GiftCard> findByCode(String code);
    List<GiftCard> findByStatus(GiftCardStatus status);
}

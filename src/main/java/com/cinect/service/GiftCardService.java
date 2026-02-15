package com.cinect.service;

import com.cinect.dto.response.GiftCardResponse;
import com.cinect.entity.GiftCard;
import com.cinect.entity.GiftTransaction;
import com.cinect.entity.User;
import com.cinect.entity.enums.GiftCardStatus;
import com.cinect.exception.BadRequestException;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.repository.GiftCardRepository;
import com.cinect.repository.GiftTransactionRepository;
import com.cinect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GiftCardService {

    private final GiftCardRepository giftCardRepository;
    private final GiftTransactionRepository giftTransactionRepository;
    private final UserRepository userRepository;

    public List<GiftCardResponse> findAll() {
        return giftCardRepository.findByStatus(GiftCardStatus.AVAILABLE).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public GiftCardResponse findById(UUID id) {
        GiftCard card = giftCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gift card", "id", id));
        return toResponse(card);
    }

    public GiftCardResponse findByCode(String code) {
        GiftCard card = giftCardRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Gift card", "code", code));
        return toResponse(card);
    }

    @Transactional
    public GiftCardResponse purchase(UUID giftCardId, UUID buyerId, String recipientEmail, String message) {
        GiftCard card = giftCardRepository.findById(giftCardId)
                .orElseThrow(() -> new ResourceNotFoundException("Gift card", "id", giftCardId));

        if (card.getStatus() != GiftCardStatus.AVAILABLE) {
            throw new BadRequestException("Gift card is not available for purchase");
        }

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", buyerId));

        // Generate unique code for this gift card instance
        String uniqueCode = "GC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        card.setCode(uniqueCode);
        card.setStatus(GiftCardStatus.SOLD_OUT);
        giftCardRepository.save(card);

        GiftTransaction tx = GiftTransaction.builder()
                .giftCard(card)
                .buyer(buyer)
                .recipientEmail(recipientEmail)
                .message(message)
                .purchasedAt(Instant.now())
                .build();
        giftTransactionRepository.save(tx);

        return toResponse(card);
    }

    @Transactional
    public BigDecimal redeem(String code, UUID userId) {
        GiftCard card = giftCardRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Gift card", "code", code));

        if (card.getStatus() == GiftCardStatus.REDEEMED) {
            throw new BadRequestException("Gift card has already been redeemed");
        }
        if (card.getStatus() == GiftCardStatus.EXPIRED) {
            throw new BadRequestException("Gift card has expired");
        }
        if (card.getExpiresAt() != null && card.getExpiresAt().isBefore(Instant.now())) {
            card.setStatus(GiftCardStatus.EXPIRED);
            giftCardRepository.save(card);
            throw new BadRequestException("Gift card has expired");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        card.setStatus(GiftCardStatus.REDEEMED);
        giftCardRepository.save(card);

        // Find the transaction and mark as redeemed
        List<GiftTransaction> transactions = giftTransactionRepository.findByBuyerId(userId);
        // Also check transactions where this card was purchased
        GiftTransaction tx = giftTransactionRepository.findAll().stream()
                .filter(t -> t.getGiftCard().getId().equals(card.getId()) && t.getRedeemedAt() == null)
                .findFirst()
                .orElse(null);

        if (tx != null) {
            tx.setRedeemedAt(Instant.now());
            tx.setRedeemedBy(user);
            giftTransactionRepository.save(tx);
        }

        return card.getValue();
    }

    public BigDecimal getBalance(String code) {
        GiftCard card = giftCardRepository.findByCode(code).orElse(null);

        if (card == null) {
            return BigDecimal.ZERO;
        }
        if (card.getStatus() == GiftCardStatus.REDEEMED) {
            return BigDecimal.ZERO;
        }
        if (card.getStatus() == GiftCardStatus.EXPIRED) {
            return BigDecimal.ZERO;
        }
        if (card.getExpiresAt() != null && card.getExpiresAt().isBefore(Instant.now())) {
            return BigDecimal.ZERO;
        }
        return card.getValue();
    }

    private GiftCardResponse toResponse(GiftCard card) {
        return GiftCardResponse.builder()
                .id(card.getId())
                .title(card.getTitle())
                .description(card.getDescription())
                .imageUrl(card.getImageUrl())
                .value(card.getValue())
                .price(card.getPrice())
                .code(card.getCode())
                .status(card.getStatus())
                .expiresAt(card.getExpiresAt())
                .createdAt(card.getCreatedAt())
                .build();
    }
}

package com.cinect.dto.request;

import com.cinect.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateBookingRequest {
    @NotNull
    private UUID showtimeId;
    @NotNull
    private UUID holdId;
    @NotEmpty
    private List<UUID> seatIds;
    private List<SnackItemRequest> snacks;
    private String promotionCode;
    @Builder.Default
    private Integer pointsUsed = 0;
    private String giftCardCode;
    @NotNull
    private PaymentMethod paymentMethod;
}

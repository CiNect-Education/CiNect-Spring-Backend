package com.cinect.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookingSnackResponse {
    private String name;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}

package com.cinect.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoldSeatLineResponse {
    private UUID id;
    private String row;
    private Integer number;
    private String type;
    private BigDecimal price;
}

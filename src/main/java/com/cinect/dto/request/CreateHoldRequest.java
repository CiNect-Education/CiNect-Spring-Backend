package com.cinect.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateHoldRequest {
    @NotNull
    private UUID showtimeId;
    @NotEmpty
    private List<UUID> seatIds;
}

package com.cinect.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SnackItemRequest {
    @NotNull
    private UUID snackId;
    @NotNull @Min(1)
    private Integer quantity;
}

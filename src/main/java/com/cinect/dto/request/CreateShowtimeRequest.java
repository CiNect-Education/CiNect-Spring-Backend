package com.cinect.dto.request;

import com.cinect.entity.enums.RoomFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateShowtimeRequest {
    @NotNull
    private UUID movieId;
    @NotNull
    private UUID roomId;
    @NotNull
    private UUID cinemaId;
    @NotNull
    private Instant startTime;
    @NotNull @DecimalMin("0")
    private BigDecimal basePrice;
    @NotNull
    private RoomFormat format;
    private String language;
    private String subtitles;
}

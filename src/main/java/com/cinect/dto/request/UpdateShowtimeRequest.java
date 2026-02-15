package com.cinect.dto.request;

import com.cinect.entity.enums.RoomFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateShowtimeRequest {
    private UUID movieId;
    private UUID roomId;
    private UUID cinemaId;
    private Instant startTime;
    private Instant endTime;
    private BigDecimal basePrice;
    private RoomFormat format;
    private String language;
    private String subtitles;
    private Boolean isActive;
}

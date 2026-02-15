package com.cinect.dto.response;

import com.cinect.entity.enums.RoomFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ShowtimeResponse {
    private UUID id;
    private UUID movieId;
    private String movieTitle;
    private UUID roomId;
    private String roomName;
    private UUID cinemaId;
    private String cinemaName;
    private Instant startTime;
    private Instant endTime;
    private BigDecimal basePrice;
    private RoomFormat format;
    private String language;
    private String subtitles;
    private Boolean isActive;
    private Boolean memberExclusive;
    private Instant createdAt;
}

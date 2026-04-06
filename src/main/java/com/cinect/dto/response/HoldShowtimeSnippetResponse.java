package com.cinect.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoldShowtimeSnippetResponse {
    private String movieTitle;
    private String cinemaName;
    private String roomName;
    private Instant startTime;
    private String format;
    private UUID cinemaId;
}

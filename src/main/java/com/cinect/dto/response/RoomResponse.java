package com.cinect.dto.response;

import com.cinect.entity.enums.RoomFormat;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoomResponse {
    private UUID id;
    private UUID cinemaId;
    private String name;
    private RoomFormat format;
    private Integer totalSeats;
    private Integer rows;
    private Integer columns;
    private Boolean isActive;
    private List<SeatResponse> seats;
    private Instant createdAt;
}

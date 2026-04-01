package com.cinect.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

/**
 * Body for {@code POST /admin/rooms} (Nest-compatible direct create with {@code cinemaId}).
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminDirectRoomRequest {
    @NotNull
    private UUID cinemaId;
    private String name;
    /** e.g. 2D, 3D, IMAX, 4DX, DOLBY or enum name */
    private String format;
    private Integer totalSeats;
    private Integer rows;
    private Integer columns;
    private Boolean isActive;
}

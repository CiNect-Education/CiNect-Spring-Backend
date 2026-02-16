package com.cinect.dto.request;

import com.cinect.entity.enums.RoomFormat;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateRoomRequest {
    private String name;
    private RoomFormat format;
    private Integer rows;
    private Integer columns;
    private Boolean isActive;
}

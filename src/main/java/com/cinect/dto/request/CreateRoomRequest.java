package com.cinect.dto.request;

import com.cinect.entity.enums.RoomFormat;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateRoomRequest {
    @NotBlank
    private String name;
    @NotNull
    private RoomFormat format;
    @NotNull @Min(1)
    private Integer rows;
    @NotNull @Min(1)
    private Integer columns;
    private JsonNode seats; // JSON with seat layout: { row: "A", number: 1, type: "STANDARD", ... }
}

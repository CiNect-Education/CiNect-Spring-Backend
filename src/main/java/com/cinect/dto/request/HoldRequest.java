package com.cinect.dto.request;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class HoldRequest {
    private UUID showtimeId;
    private List<UUID> seatIds;
}
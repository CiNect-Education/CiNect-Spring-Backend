package com.cinect.entity;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
public class HoldSeatId implements Serializable {
    private UUID holdId;
    private UUID seatId;
}

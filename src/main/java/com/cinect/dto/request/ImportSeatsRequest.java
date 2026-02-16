package com.cinect.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ImportSeatsRequest {
    /** JSON array of seat definitions: { "row": "A", "number": 1, "type": "STANDARD", "isAisle": false } */
    private List<SeatImportItem> seats;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SeatImportItem {
        private String row;
        private Integer number;
        private String type;
        private Boolean isAisle;
    }
}

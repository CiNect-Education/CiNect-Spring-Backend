package com.cinect.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateReviewRequest {
    @NotNull
    private UUID movieId;
    @NotNull @Min(1) @Max(10)
    private Integer rating;
    @NotBlank
    private String content;
}

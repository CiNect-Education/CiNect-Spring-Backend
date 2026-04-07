package com.cinect.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatbotRequest {
    @NotBlank
    @Size(max = 2000)
    private String message;

    @Builder.Default
    private String locale = "vi";
}

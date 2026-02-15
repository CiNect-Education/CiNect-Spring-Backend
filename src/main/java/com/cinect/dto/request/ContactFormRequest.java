package com.cinect.dto.request;

import com.cinect.entity.enums.SupportCategory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ContactFormRequest {
    @NotBlank
    private String name;
    @NotBlank @Email
    private String email;
    @NotBlank
    private String subject;
    @Builder.Default
    private SupportCategory category = SupportCategory.OTHER;
    @NotBlank
    private String message;
    private UUID bookingId;
}

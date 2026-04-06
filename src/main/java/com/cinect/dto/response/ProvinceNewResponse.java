package com.cinect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceNewResponse {
    private UUID id;
    private String code;
    private String nameVi;
    private String nameEn;
    private Integer sortOrder;
}

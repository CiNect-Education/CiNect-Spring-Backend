package com.cinect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceNewSummaryResponse {
    private String code;
    private String nameVi;
    private String nameEn;
}

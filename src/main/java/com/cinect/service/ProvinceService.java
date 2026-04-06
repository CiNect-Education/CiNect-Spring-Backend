package com.cinect.service;

import com.cinect.dto.response.ProvinceLegacyResponse;
import com.cinect.dto.response.ProvinceNewResponse;
import com.cinect.dto.response.ProvinceNewSummaryResponse;
import com.cinect.repository.ProvinceLegacyRepository;
import com.cinect.repository.ProvinceNewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProvinceService {

    private final ProvinceNewRepository provinceNewRepository;
    private final ProvinceLegacyRepository provinceLegacyRepository;

    @Transactional(readOnly = true)
    public List<ProvinceNewResponse> listNew() {
        return provinceNewRepository.findAllByOrderBySortOrderAsc().stream()
                .map(p -> ProvinceNewResponse.builder()
                        .id(p.getId())
                        .code(p.getCode())
                        .nameVi(p.getNameVi())
                        .nameEn(p.getNameEn())
                        .sortOrder(p.getSortOrder())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProvinceLegacyResponse> listLegacy() {
        return provinceLegacyRepository.findAllOrderedWithNew().stream()
                .map(l -> {
                    var n = l.getProvinceNew();
                    return ProvinceLegacyResponse.builder()
                            .id(l.getId())
                            .code(l.getCode())
                            .nameVi(l.getNameVi())
                            .nameEn(l.getNameEn())
                            .provinceNew(ProvinceNewSummaryResponse.builder()
                                    .code(n.getCode())
                                    .nameVi(n.getNameVi())
                                    .nameEn(n.getNameEn())
                                    .build())
                            .build();
                })
                .collect(Collectors.toList());
    }
}

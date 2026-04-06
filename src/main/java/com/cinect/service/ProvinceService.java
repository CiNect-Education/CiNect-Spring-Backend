package com.cinect.service;

import com.cinect.dto.response.ProvinceLegacyResponse;
import com.cinect.dto.response.ProvinceNewResponse;
import com.cinect.dto.response.ProvinceNewSummaryResponse;
import com.cinect.repository.ProvinceLegacyRepository;
import com.cinect.repository.ProvinceNewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProvinceService {

    private final ProvinceNewRepository provinceNewRepository;
    private final ProvinceLegacyRepository provinceLegacyRepository;
    private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-z0-9]+(-[a-z0-9]+)*$");
    private static final Map<String, String> SHORT_ALIASES = Map.ofEntries(
            Map.entry("hcm", "ho-chi-minh-city"),
            Map.entry("hn", "ha-noi"),
            Map.entry("dn", "da-nang"),
            Map.entry("hp", "hai-phong"),
            Map.entry("ct", "can-tho"),
            Map.entry("bd", "ho-chi-minh-city"),
            Map.entry("nt", "khanh-hoa"),
            Map.entry("vt", "ho-chi-minh-city")
    );

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

    @Transactional(readOnly = true)
    public String resolveToNewCode(String cityOrProvince) {
        if (cityOrProvince == null || cityOrProvince.isBlank()) {
            return null;
        }
        String raw = cityOrProvince.trim();
        String low = raw.toLowerCase(Locale.ROOT);

        if (SHORT_ALIASES.containsKey(low)) {
            return SHORT_ALIASES.get(low);
        }
        var byLegacyCode = provinceLegacyRepository.findByCodeWithNew(low);
        if (byLegacyCode.isPresent()) {
            return byLegacyCode.get().getProvinceNew().getCode();
        }
        var byNewCode = provinceNewRepository.findByCode(low);
        if (byNewCode.isPresent()) {
            return byNewCode.get().getCode();
        }
        var byLegacyNameRaw = provinceLegacyRepository.findByNameWithNew(raw);
        if (byLegacyNameRaw.isPresent()) {
            return byLegacyNameRaw.get().getProvinceNew().getCode();
        }
        var byNewNameRaw = provinceNewRepository.findByName(raw);
        if (byNewNameRaw.isPresent()) {
            return byNewNameRaw.get().getCode();
        }

        String normalized = normalize(raw);
        var byLegacyName = provinceLegacyRepository.findAllOrderedWithNew().stream()
                .filter(p -> normalize(p.getNameVi()).equals(normalized) || normalize(p.getNameEn()).equals(normalized))
                .findFirst();
        if (byLegacyName.isPresent()) {
            return byLegacyName.get().getProvinceNew().getCode();
        }
        var byNewName = provinceNewRepository.findAllByOrderBySortOrderAsc().stream()
                .filter(p -> normalize(p.getNameVi()).equals(normalized) || normalize(p.getNameEn()).equals(normalized))
                .findFirst();
        if (byNewName.isPresent()) {
            return byNewName.get().getCode();
        }

        if (SLUG_PATTERN.matcher(low).matches()) {
            return low;
        }
        return null;
    }

    private static String normalize(String s) {
        String nfd = Normalizer.normalize(s.trim(), Normalizer.Form.NFD);
        String noMarks = nfd.replaceAll("\\p{M}+", "");
        return noMarks.toLowerCase(Locale.ROOT);
    }
}

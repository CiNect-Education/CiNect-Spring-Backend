package com.cinect.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "provinces_new")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvinceNew extends BaseEntity {

    @Column(nullable = false, unique = true, length = 64)
    private String code;

    @Column(name = "name_vi", nullable = false)
    private String nameVi;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;
}

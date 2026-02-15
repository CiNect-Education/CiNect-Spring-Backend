package com.cinect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cinemas")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Cinema extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false, columnDefinition = "text")
    private String address;

    @Column(nullable = false)
    private String city;

    private String district;
    private String phone;
    private String email;

    @Column(name = "image_url")
    private String imageUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> amenities;

    private Double latitude;
    private Double longitude;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Room> rooms = new ArrayList<>();
}

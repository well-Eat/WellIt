package com.wellit.project.store;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "allstore")
@Getter
@Setter
public class AllStore {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stoId;

    @Column(nullable = false)
    private String stoName;

    private String stoTitle;
    
    @Lob
    private String stoContent;

    private String stoCategory;
    
    @Column(name = "view_count", columnDefinition = "integer default 0")
    private int viewCount;

    @Column(name = "recommendation_count", columnDefinition = "integer default 0")
    private int recommendationCount;

    private String stoRegionProvince;
    private String stoRegionCity;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private String stoContact;
    private String stoAddress;
    private String stoImage;
    private String stoOperatingHours;
    private String stoClosedDays;
    private String stoRecommendedMenu;
    private String stoParkingInfo;
    private Double stoLatitude;
    private Double stoLongitude;
    
    @Column(name = "is_open")
    private boolean isOpen;
    
    @Column(name = "kakao_store_id", nullable = false)
    private String kakaoStoreId; // 카카오맵 가게 고유 ID
}
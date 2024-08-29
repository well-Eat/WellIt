package com.study.project.store;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "store")
@Getter
@Setter
public class Store {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String title;
    
    @Lob
    private String content;

    private String category;

    @Column(name = "view_count", columnDefinition = "NUMBER DEFAULT 0")
    private int viewCount = 0;

    @Column(name = "recommendation_count", columnDefinition = "NUMBER DEFAULT 0")
    private int recommendationCount = 0;

    private String regionProvince;
    private String regionCity;

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    private String contact;
    private String address;
    private String image;
    private String operatingHours;
    private String closedDays;
    private String recommendedMenu;
    private String parkingInfo;
    private Double latitude;
    private Double longitude;

    @Column(name = "is_open", columnDefinition = "CHAR(1) DEFAULT 'Y'")
    private String isOpen = "Y";

    // Getters and Setters
}
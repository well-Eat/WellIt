package com.wellit.project.store;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "store")
@Getter
@Setter
public class Store {
	
	// 기존 생성자
    public Store() {
    }
	
	// 장소 ID를 매개변수로 받는 생성자
    public Store(Long stoId) {
        this.stoId = stoId;
    }
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stoId")
    private Long stoId;

    @Column(nullable = false)
    private String stoName;

    private String stoTitle;
    
    @Lob
    private String stoContent;

    private String stoCategory;

    @Column(name = "view_count", columnDefinition = "NUMBER DEFAULT 0")
    private int stoViewCount = 0;

    @Column(name = "recommendation_count", columnDefinition = "NUMBER DEFAULT 0")
    private double stoRecommendationCount = 0;

    private String stoRegionProvince;
    private String stoRegionCity;

    @Column(name = "created_at", updatable = false)
    private Timestamp stoCreatedAt;

    @Column(name = "updated_at")
    private Timestamp stoUpdatedAt;

    private String stoContact;
    private String stoAddress;
    private String stoImage;
    private String stoOperatingHours;
    private String stoClosedDays;
    private String stoRecommendedMenu;
    private String stoParkingInfo;
    private Double stoLatitude;
    private Double stoLongitude;

    @Column(name = "is_open", columnDefinition = "CHAR(1) DEFAULT 'Y'")
    private String stoIsOpen = "Y";

    public Store(Integer placeId) {
		// TODO Auto-generated constructor stub
	}

	@PrePersist
    protected void onCreate() {
        stoCreatedAt = Timestamp.valueOf(LocalDateTime.now());
        stoUpdatedAt = Timestamp.valueOf(LocalDateTime.now());
    }

    @PreUpdate
    protected void onUpdate() {
        stoUpdatedAt = Timestamp.valueOf(LocalDateTime.now());
    }
    
}
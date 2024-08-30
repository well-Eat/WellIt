package com.wellit.project.store;

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
	
	// 기존 생성자
    public Store() {
    }
	
	// 장소 ID를 매개변수로 받는 생성자
    public Store(Long sto_id) {
        this.sto_id = sto_id;
    }
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sto_id;

    @Column(nullable = false)
    private String sto_name;

    private String sto_title;
    
    @Lob
    private String sto_content;

    private String sto_category;

    @Column(name = "view_count", columnDefinition = "NUMBER DEFAULT 0")
    private int sto_viewCount = 0;

    @Column(name = "recommendation_count", columnDefinition = "NUMBER DEFAULT 0")
    private int sto_recommendationCount = 0;

    private String sto_regionProvince;
    private String sto_regionCity;

    @Column(name = "created_at", updatable = false)
    private Timestamp sto_createdAt;

    @Column(name = "updated_at")
    private Timestamp sto_updatedAt;

    private String sto_contact;
    private String sto_address;
    private String sto_image;
    private String sto_operatingHours;
    private String sto_closedDays;
    private String sto_recommendedMenu;
    private String sto_parkingInfo;
    private Double sto_latitude;
    private Double sto_longitude;

    @Column(name = "is_open", columnDefinition = "CHAR(1) DEFAULT 'Y'")
    private String sto_isOpen = "Y";

    public Store(Integer placeId) {
		// TODO Auto-generated constructor stub
	}

	@PrePersist
    protected void onCreate() {
        sto_createdAt = Timestamp.valueOf(LocalDateTime.now());
        sto_updatedAt = Timestamp.valueOf(LocalDateTime.now());
    }

    @PreUpdate
    protected void onUpdate() {
        sto_updatedAt = Timestamp.valueOf(LocalDateTime.now());
    }
}
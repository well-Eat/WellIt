package com.wellit.project.store;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "allstore")
@Getter
@Setter
public class AllStore {
    
	public AllStore() {
		
	}
    public AllStore(Long stoId) {
    	this.stoId = stoId;
	}

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
    private double recommendationCount;

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
    private String stoVegetarianType; 
    
    @Column(name = "is_open")
    private boolean isOpen;
    
    @Column(name = "kakao_store_id", nullable = false, unique = true)
    private String kakaoStoreId; // 카카오맵 가게 고유 ID
    
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("createdAt DESC") // 생성일 기준으로 내림차순 정렬
    @JsonManagedReference("reviews")
    private List<StoreReview> storeReviews;
    
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("favorites")
    private List<FavoriteStore> favoriteStores;
    
    @OneToMany(mappedBy = "allStore", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("store-reservations")
    private List<StoreReservation> reservations; // 가게에 대한 예약 목록
}
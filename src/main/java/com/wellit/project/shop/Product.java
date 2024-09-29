package com.wellit.project.shop;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.wellit.project.order.CartItem;
import com.wellit.project.order.OrderItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "product")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prodId; //상품 idx

    @Enumerated(EnumType.STRING)
    private ProdStatus prodStatus = ProdStatus.AVAILABLE; //상품 판매 여부

    private String prodMainImg; //상품썸네일이미지 url
    private String prodName; //상품명
    private String prodDesc; //상품 설명
    private Integer prodOrgPrice; //상품 원래 가격

    @ColumnDefault("0")
    private Double prodDiscount; //할인율
    private String prodCate; //카테고리

    private Integer prodFinalPrice; //판매 가격(할인율 반영)

    private Long prodStock; //재고수량

    private Integer viewCnt; //조회수


    // 여러 개의 상품 이미지
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonManagedReference("product-images") // 이름 설정
    private List<ProdImage> prodImages; // 이미지 리스트


    //상품 세부 정보
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonManagedReference("product-info") // 이름 설정
    private List<ProdInfo> prodInfoList;


    private LocalDateTime createdAt; //등록 일자
    private LocalDateTime updatedAt; //수정 일자

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteProduct> favoriteProductList = new ArrayList<>();


}

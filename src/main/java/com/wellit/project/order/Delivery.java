package com.wellit.project.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    // 배송 상태
    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;


    private String deliveryAddr; // 배송 주소
    private String deliveryName; // 받는분
    private String deliveryPhone; // 받는분 연락처

    private String deliveryMsg; //배송 메시지

    private String invoiceNum; //송장번호


    private LocalDateTime createdAt; //주문 일자
    private LocalDateTime updatedAt; //상태 변경 시간


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @OneToOne
    @JoinColumn(name = "order_id")
    private PurchaseOrder purchaseOrder;




}

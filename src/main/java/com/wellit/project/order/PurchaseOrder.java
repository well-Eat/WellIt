package com.wellit.project.order;

import com.wellit.project.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class PurchaseOrder {

    @Id
    private String orderId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태


    /* 금액 관련 필드 */
    private Integer orgPrice; //원래 상품 가격
    private Integer discPrice; //할인 금액(음수)
    private Integer deliveryFee; //배송비
    private Integer totalPrice; // 최종 합계 금액 (orgPrice + discPrice +deliveryFee)
    private Integer milePay; //마일리지 사용금액 (음수)
    private Integer totalPay; // 최종 지불 금액 (totalPrice + milePay)



    // 주문자
    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;


    // 주문 일시
    private LocalDateTime createdAt; //주문 일자
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

    //결제정보
    @OneToOne(mappedBy = "purchaseOrder", cascade=CascadeType.ALL)
    private Payment payment;

    //배송정보
    @OneToOne(mappedBy = "purchaseOrder", cascade=CascadeType.ALL)
    private Delivery delivery;


    //주문 상품 리스트
    @OneToMany(mappedBy = "purchaseOrder", cascade=CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems = new ArrayList<>();




    public PurchaseOrder() {
        this.orderId = generateOrderId();
    }

    //주문번호 구성 : 날짜8자 + 시분초 6자+ '_' + 랜덤 8자
    private String generateOrderId() {
        String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);  // UUID 일부만 사용
        return date + uuid;  // 날짜 + UUID 결합
    }




}



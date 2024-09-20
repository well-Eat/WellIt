package com.wellit.project.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class CancelOrderRequest {
    //구매자 주문 취소 요청

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId; //취소 요청 주문번호

    private String cancelReason; //취소 신청 사유

    private LocalDateTime createdAt; //취소 신청 일자


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


}

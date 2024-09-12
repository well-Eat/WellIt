package com.wellit.project.order;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private PurchaseOrder purchaseOrder;

    private int amount;  // 최종 결제 금액

    //@Enumerated(EnumType.STRING)
    //private PaymentStatus status;

    //결제일시
    private LocalDateTime paymentDate;

    //결제방법
}

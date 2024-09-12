package com.wellit.project.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private PurchaseOrder purchaseOrder;

    private Integer paidAmount;

    private String impUid;
    private String merchantUid;
    private String buyerEmail;
    private String buyerName;
    private String paymentStatus;
    private boolean success;
    private String payMethod;
    private String pgProvider;


    // 결제 일시
    private LocalDateTime createdAt; //결제 일자


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }





}

/*
"apply_num":"","bank_name":null,
        buyer_addr":"""buyer_email":"eossummershin19@gmail.com""buyer_name":"김유저""buyer_postcode":"""buyer_tel":"""card_name":"null
        card_number":"""card_quota":"0
        currency":"KRW""custom_data":"null
        imp_uid":"imp_134673954806""merchant_uid":"IMP10541885""name":"상품명""paid_amount":"100
        paid_at":"1726019702
        pay_method":"point""pg_provider":"kakaopay""pg_tid":"T6e0f85a2af358366089""pg_type":"payment""receipt_url":"https://mockup-pg-web.kakao.com/v1/confirmation/p/T6e0f85a2af358366089/6d8091bcbcb702a69e54088dd02ec332ba9de982799a52f9e4c480f4b640c28e""status":"paid""success":true
*/


package com.wellit.project.order;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PoHistoryForm {

    //private PurchaseOrder po;

    //PO 정보
    private String orderId;
    private OrderStatus orderStatus;
    private Integer totalPrice; // 총 금액
//    private Integer totalPay;
//    private LocalDateTime createdAt;
//
    //Payment 정보
//    private Integer paidAmount;
//    private String paymentStatus;
    private String  paymentStatus; //결제 상태
    private LocalDateTime paidAt; // 구매시간

//
    //Delivery 정보
    private DeliveryStatus deliveryStatus;
//    private String addr1;
//    private String addr2;
//    private String deliveryName;
//    private String deliveryPhone;
//    private String deliveryMsg;
//    private String InvoiceNum;

    //구매 아이템
    private List<OrderItemDTO> orderItems;

    public PoHistoryForm (){}

}

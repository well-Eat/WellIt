package com.wellit.project.order;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PoProcessForm {

    //private PurchaseOrder po;

    //PO 정보
    private String orderId;
    private String orderStatus;

    //주문자 정보
    private String memberName;
    private String memberId;
    private String memberPhone;


    /* 금액 관련 필드 */
    private Integer orgPrice; //원래 상품 가격
    private Integer discPrice; //할인 금액(음수)
    private Integer finalPrice; //할인 반영 상품 금액
    private Integer deliveryFee; //배송비
    private Integer totalPrice; // 최종 합계 금액 (orgPrice + discPrice +deliveryFee)
    private Integer milePay; //마일리지 사용금액 (음수)
    private Integer totalPay; // 최종 지불 금액 (totalPrice + milePay)


    //Payment 정보
    private String  paymentStatus; //결제 상태
    private LocalDateTime paidAt; // 구매시간
    private String pgProvider; //결제 pg사
    private String impUid; //아임포트 uid
    private String merchantUid; //아임포트 결제 고유번호

//
    //Delivery 정보
    private DeliveryStatus deliveryStatus;
    private String addr1;
    private String addr2;
    private String deliveryName;
    private String deliveryPhone;
    private String deliveryMsg;
    private String InvoiceNum;

    //구매 아이템
    private List<OrderItemProcessDTO> orderItems;

    public PoProcessForm(){}

}

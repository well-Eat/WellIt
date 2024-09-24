package com.wellit.project.order;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PoForm {

    private String orderId;


    private String addr1;
    private String addr2;

    private String deliveryName;

    // 받는분 연락처
    private String phone1;
    private String phone2;
    private String phone3;
    private String deliveryPhone;
    private String deliveryMsg; //배송 메시지



    /* 금액 관련 필드 */
    private Integer finalPrice; //할인 적용 가격 = 주문금액
    private Integer orgPrice; //원래 상품 가격 = 상품금액
    private Integer discPrice; //할인 금액(음수) = 상품할인금액
    private Integer deliveryFee; //배송비
    private Integer totalPrice; // 최종 합계 금액 (orgPrice + discPrice +deliveryFee)
    private Integer milePay; //마일리지 사용금액 (음수)
    private Integer totalPay; // 최종 지불 금액 (totalPrice + milePay)


}

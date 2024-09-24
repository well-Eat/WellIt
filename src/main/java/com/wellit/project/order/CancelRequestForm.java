package com.wellit.project.order;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CancelRequestForm {
    //주문 취소 테이블용 DTO 클래스

    private Long id; // 취소 ID

    private String orderId; //취소 요청 주문번호

    private String cancelReason; //취소 신청 사유

    private LocalDateTime cancelRequestAt; //취소 신청 일자

    private OrderStatus orderStatus; //주문 상태

    private LocalDateTime orderRequestAt; //주문 시간

    private Integer totalPay; //총 지불 금액

}

package com.wellit.project.order;


import lombok.Getter;
import lombok.Setter;

//주문아이템dto
@Getter
@Setter
public class OrderItemQuantity {

    private Long prodId;

    private Integer quantity; //구매수량

    private boolean booleanOrder; // 주문 아이템 포함여부

    private Integer sumOrgPrice; // 원래 상품 가격 * 수량
    private Integer sumDiscPrice; //해당 상품의 할인 금액(음수)
}

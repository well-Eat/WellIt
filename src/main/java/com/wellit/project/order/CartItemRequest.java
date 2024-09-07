package com.wellit.project.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequest {

    private Long prodId;
    private int quantity;  // 상품의 수량 정보

}

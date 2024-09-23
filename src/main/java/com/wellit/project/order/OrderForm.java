package com.wellit.project.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderForm {



    @Size(min = 1, message = "장바구니에 아이템이 없습니다.")
    private List<OrderItemQuantity> orderItemQuantityList = new ArrayList<>(); //아이템리스트 (prodId, 수량, checked여부)





}

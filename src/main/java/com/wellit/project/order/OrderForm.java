package com.wellit.project.order;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderForm {


    private List<OrderItemQuantity> orderItemQuantityList = new ArrayList<>(); //아이템리스트 (prodId, 수량, checked여부)

    private String addr1;
    private String addr2;



}

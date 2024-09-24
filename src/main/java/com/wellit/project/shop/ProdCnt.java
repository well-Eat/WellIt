package com.wellit.project.shop;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProdCnt {
    private Long prodId; // 상품id
    private Integer revCnt; //리뷰 수
    private Integer favoriteCnt; //찜한 상품 수
}

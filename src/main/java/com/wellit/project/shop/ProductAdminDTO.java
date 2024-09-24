package com.wellit.project.shop;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class ProductAdminDTO {

    private Long prodId; //상품 idx

    private String prodMainImg; //상품썸네일이미지 url
    private String prodName; //상품명
    private Integer prodOrgPrice; //상품 원래 가격


    private Double prodDiscount; //할인율
    private String prodCate; //카테고리

    private Integer prodFinalPrice; //판매 가격(할인율 반영)

    private Long prodStock; //재고수량

    private Integer viewCnt; //조회수


    private LocalDateTime createdAt; //등록 일자
    private LocalDateTime updatedAt; //수정 일자

    private Integer sumQuantity; //판매량합계
    private Integer totalFinalPrice; //상품별 매출금액 합계

}

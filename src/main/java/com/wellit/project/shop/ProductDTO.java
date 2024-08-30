package com.wellit.project.shop;

import lombok.Data;

@Data
public class ProductDTO {

    private Integer prodId;
    private String imgSrc;
    private String prodName;
    private String prodDesc;
    private Integer prodOrgPrice;
    private Double prodDiscount;
    private Integer prodReview;
    private String prodCate;
    private String prodDetailImg;

    private Integer prodFinalPrice;

}

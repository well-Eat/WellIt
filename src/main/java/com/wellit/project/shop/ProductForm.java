package com.wellit.project.shop;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductForm {

    private Long prodId; //상품 idx

    private MultipartFile prodMainImg; //상품썸네일이미지 url

    private String prodName; //상품명
    private String prodDesc; //상품 설명
    private Integer prodOrgPrice; //상품 원래 가격

    private Double prodDiscount; //할인율
    private String prodCate; //카테고리

    private String prodContent;

    private Integer prodFinalPrice; //판매 가격(할인율 반영)

    private List<MultipartFile> prodImages; // 이미지 리스트


    //상품 세부 정보
    private List<ProdInfo> prodInfoList;




    //Product 와 연결하는 생성자
    public ProductForm(Product product) {
        this.prodId = product.getProdId();
        this.prodName = product.getProdName();
        this.prodDesc = product.getProdDesc();
        this.prodOrgPrice = product.getProdOrgPrice();
        this.prodDiscount = product.getProdDiscount();
        this.prodCate = product.getProdCate();
        this.prodContent = product.getProdContent();
        this.prodFinalPrice = product.getProdFinalPrice();
        this.prodInfoList = product.getProdInfoList();
    }

}

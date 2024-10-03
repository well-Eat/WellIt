package com.wellit.project.shop;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductForm {

    private Long prodId; //상품 idx

    private ProdStatus prodStatus = ProdStatus.AVAILABLE; // 기본값: 판매중; //상품 판매 상태

    private MultipartFile prodMainImg; //상품썸네일이미지 url

    @NotNull
    private String prodName; //상품명

    private String prodDesc; //상품 설명

    @NotNull
    @Min(100)
    private Integer prodOrgPrice = 10000; //상품 원래 가격 : 100원 미만 시 할인율 계산할때 오류 발생 가능성 있음


    @Min(0)
    private Double prodDiscount = 0.0; //할인율
    private String prodCate; //카테고리


    private Integer prodFinalPrice; //판매 가격(할인율 반영)

    @Min(0)
    @NotNull
    private Long prodStock = 100L; //재고수량


    private List<MultipartFile> prodImages; // 이미지 리스트


    //상품 세부 정보
    private List<ProdInfo> prodInfoList;




    //Product 와 연결하는 생성자
    public ProductForm(Product product) {
        this.prodId = product.getProdId();
        this.prodStatus = product.getProdStatus();
        this.prodName = product.getProdName();
        this.prodDesc = product.getProdDesc();
        this.prodOrgPrice = product.getProdOrgPrice();
        this.prodDiscount = (product.getProdDiscount() != null) ? product.getProdDiscount() : 0.0;
        this.prodStock = product.getProdStock();
        this.prodCate = product.getProdCate();
        this.prodFinalPrice = product.getProdFinalPrice();
        this.prodInfoList = product.getProdInfoList();
    }


}

package com.study.project.shop;

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

}

/*

<!-- 상품(prodItem) : of003 -->
<div class="prodItem col mb-4 of001" data-prodcate="organicfood" id="of003">
<div class="card">

<!-- 상품이미지 -->
<div class="card-img-wrapper rounded-2 mb-3">
<div class="card-img-box"></div>
</div>

<!-- 담기버튼 -->
<a href="javascript:void(0);" class="addCartBtn btn btn-outline-secondary"><i class="fa-solid fa-cart-shopping" style="color: #666;"></i> 담기</a>

<!-- 상품설명 -->
<div class="card-body">
<h5 class="prodName card-title">[웨릿농장] 맛있는 국내산 햇감자 organicfood</h5>
<p class="prodDesc card-text">자연이 준 최고의 선물, 웨릿 오가닉 감자를 만나보세요</p>
<p class="prodOrgPrice card-text">50,000원</p>
<p class="prodDiscount card-text mr-2">20%</p>
<p class="prodFinalPrice card-text">40,000원</p>
<p class="prodReview card-text"><i class="fa-regular fa-comment-dots"></i> 9,999+</p>
<div class="prodTags">

</div>

</div> <!-- //card-body -->
</div> <!-- //card -->
</div> <!-- ///////// prodItem -->*/

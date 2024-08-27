package com.study.project.shop;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShopService {

    /*상품 리스트 리턴*/
    public List<ProductDTO> getProdCateList(){

        List<ProductDTO> prodList = makingTestProdDTO(); //테스트용 임시 데이터 만드는 메서드

        return prodList;
    }

    /*상품 리스트 임시 dto 메서드*/
    private List<ProductDTO> makingTestProdDTO(){

        List<ProductDTO> prodList = new ArrayList<>();

        ProductDTO p1 = new ProductDTO();
        p1.setProdId(1001);
        p1.setImgSrc("http://yoonseo35.dothome.co.kr/wellit/imgs/products/p0001_thumb.png");
        p1.setProdName("밀키트 아이템");
        p1.setProdDesc("진짜 간편해요!");
        p1.setProdOrgPrice(10000);
        p1.setProdDiscount(0.2);
        p1.setProdReview(365);
        p1.setProdCate("mealkit");
        prodList.add(p1);


        ProductDTO p2 = new ProductDTO();
        p2.setProdId(1002);
        p2.setImgSrc("http://yoonseo35.dothome.co.kr/wellit/imgs/products/p0002_thumb.png");
        p2.setProdName("반찬배송 아이템");
        p2.setProdDesc("진짜 맛있어요!");
        p2.setProdOrgPrice(25000);
        p2.setProdDiscount(null);
        p2.setProdReview(3650);
        p2.setProdCate("sidedish");
        prodList.add(p2);

        
        ProductDTO p3 = new ProductDTO();
        p3.setProdId(1003);
        p3.setImgSrc("http://yoonseo35.dothome.co.kr/wellit/imgs/products/p0003_thumb.png");
        p3.setProdName("유기농식재료 아이템");
        p3.setProdDesc("진짜 신선해요!");
        p3.setProdOrgPrice(10000);
        p3.setProdDiscount(0.3);
        p3.setProdReview(3650);
        p3.setProdCate("organicfood");
        prodList.add(p3);

        
        ProductDTO p4 = new ProductDTO();
        p4.setProdId(1004);
        p4.setImgSrc("http://yoonseo35.dothome.co.kr/wellit/imgs/products/p0004_thumb.png");
        p4.setProdName("오가닉제품 아이템");
        p4.setProdDesc("진짜 산뜻해요!");
        p4.setProdOrgPrice(10000);
        p4.setProdDiscount(0.2);
        p4.setProdReview(365);
        p4.setProdCate("organicgoods");
        prodList.add(p4);

        
        ProductDTO p5 = new ProductDTO();
        p5.setProdId(1005);
        p5.setImgSrc("http://yoonseo35.dothome.co.kr/wellit/imgs/products/p0005_thumb.png");
        p5.setProdName("베이비 아이템");
        p5.setProdDesc("진짜 안전해요!");
        p5.setProdOrgPrice(10000);
        p5.setProdDiscount(0.2);
        p5.setProdReview(3650);
        p5.setProdCate("babygoods");
        prodList.add(p5);

        
        ProductDTO p6 = new ProductDTO();
        p6.setProdId(1006);
        p6.setImgSrc("http://yoonseo35.dothome.co.kr/wellit/imgs/products/p0006_thumb.png");
        p6.setProdName("펫 아이템");
        p6.setProdDesc("진짜 좋아해요!");
        p6.setProdOrgPrice(10000);
        p6.setProdDiscount(0.2);
        p6.setProdReview(365);
        p6.setProdCate("petgoods");
        prodList.add(p6);

        
        ProductDTO p7 = new ProductDTO();
        p7.setProdId(1007);
        p7.setImgSrc("http://yoonseo35.dothome.co.kr/wellit/imgs/products/p0007_thumb.jpg");
        p7.setProdName("웨릿굿즈 아이템");
        p7.setProdDesc("진짜 예뻐요!");
        p7.setProdOrgPrice(10000);
        p7.setProdDiscount(0.1);
        p7.setProdReview(365);
        p7.setProdCate("wellitgoods");
        prodList.add(p7);

        
        
        
        
        
        

        return prodList;
    }

}

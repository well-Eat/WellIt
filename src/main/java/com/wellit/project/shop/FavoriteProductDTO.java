package com.wellit.project.shop;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoriteProductDTO {

    private Long favoriteProductId;

    private String memberId;
    private String memberName;

    private Long prodId;
    private String prodMainImg; //썸네일 이미지 주소
    private String prodName;

}

package com.wellit.project.shop;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewCnt {
    /*상품별 리뷰 개수, 평점 dto*/

        int cnt; //리뷰 개수
        double avg; //평점 평균
}

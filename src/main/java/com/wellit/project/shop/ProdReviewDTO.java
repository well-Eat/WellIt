package com.wellit.project.shop;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ProdReviewDTO {
    private Integer revId;
    private String revText;
    private Integer revRating;
    private String writer;
    private LocalDateTime createdAt;
    private List<String> prodReviewImgList; // 이미지 리스트
}

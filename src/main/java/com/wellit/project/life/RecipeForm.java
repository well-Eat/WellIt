package com.wellit.project.life;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class RecipeForm {

    private Long id;

    private String recpName; //요리 이름
    private String recpIntroduce; //요리 간단 소개
    private String writer; //나중에 Member로 바꿀것!
    private Integer servings; //몇 인분
    private Integer cookTime; //요리 소요시간(분)
    private String recpTags; //태그
    private String difficulty; //난이도

    private List<MultipartFile> recpMainImgList; //메인 이미지 리스트

    private List<RecpIngredient> recpIngredientList; //재료 리스트

    private List<CookOrderCardForm> cookOrderCardList; //레시피 순서 카드

    private List<String> existingMainImgUrls; // 기존 메인 이미지 URL 리스트

    private List<String> existingImgIds;
    
    private List<String> existingCookOrderImgUrls; // 기존 요리 이미지 URL 리스트 추가

    
}

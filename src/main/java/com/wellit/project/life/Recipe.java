package com.wellit.project.life;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String recpName; // 요리 이름
    private String recpIntroduce; // 요리 간단 소개
    private String servings; // 몇 인분
    private Integer cookTime; // 요리 소요시간(분)
    private String recpTags; // 태그
    private String difficulty; // 난이도

    @OneToMany(mappedBy = "recipe", orphanRemoval = true)
    @JsonManagedReference("recipe-main-img") // 이름 지정
    private List<RecpMainImg> recpmainImgList; // 메인 이미지 리스트

    @OneToMany(mappedBy = "recipe", orphanRemoval = true)
    @JsonManagedReference("recipe-ingredient") // 이름 지정
    private List<RecpIngredient> recpIngredientList; // 재료 리스트

    @OneToMany(mappedBy = "recipe", orphanRemoval = true)
    @JsonManagedReference("recipe-cook-order") // 이름 지정
    private List<CookOrderCard> cookOrderCardList; // 조리 순서 카드 리스트

    private String writer; // 나중에 Member로 바꿀 것!
}


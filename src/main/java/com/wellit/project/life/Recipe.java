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

    //input
    private String recpName; //요리 이름
    private String recpIntroduce; //요리 간단 소개
    private String servings; //몇 인분
    private Integer cookTime; //요리 소요시간(분)
    private String recpTags; //태그
    private String difficulty; //난이도 -> 추후 enum으로 바꾸기

    @OneToMany(mappedBy = "recipe", orphanRemoval = true)
    @JsonManagedReference
    private List<RecpMainImg> recpmainImgList; //메인 이미지 리스트

    @OneToMany(mappedBy = "recipe", orphanRemoval = true)
    @JsonManagedReference
    private List<RecpIngredient> recpIngredientList; //재료 리스트

    @OneToMany(mappedBy = "recipe", orphanRemoval = true)
    @JsonManagedReference
    private List<CookOrderCard> cookOrderCardList;



    //자동입력
    private String writer; //나중에 Member로 바꿀것!
/*    private Integer view;
    private Integer heart;
    private Integer bookmark;*/



}

package com.wellit.project.life;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RecpMainImg {
    /*레시피 상단 인트로(메인) 이미지 저장 엔티티*/

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String imgSrc; //메인 이미지 주소
    private boolean isMain; //대표 이미지 여부
    private Integer mainImgIndex; //이미지 인덱스

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    @JsonBackReference("recipe-main-img") // 동일한 이름 지정
    private Recipe recipe;

}

package com.wellit.project.life;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CookOrderCard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer cookOrderNum; //요리 순서

    private String cookOrderImg; //요리 이미지

    private String cookOrderText; //요리 방법



    @ManyToOne
    @JsonBackReference("recipe-cook-order") // 동일한 이름 지정
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;



}

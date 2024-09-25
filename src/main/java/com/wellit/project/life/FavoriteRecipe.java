package com.wellit.project.life;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.wellit.project.member.Member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FavoriteRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JsonBackReference("favorite-recipe-member") // 동일한 이름 지정
    @JoinColumn(name = "member_id")
    private Member member; // 찜한 사용자

    @ManyToOne
    @JsonBackReference("favorite-recipe") // 동일한 이름 지정
    @JoinColumn(name = "recipe_id")
    private Recipe recipe; // 찜한 레시피
}

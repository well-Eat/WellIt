package com.wellit.project.life;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RecpIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String ingredientName; // 재료 이름
    private String amount; //재료 양

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

}

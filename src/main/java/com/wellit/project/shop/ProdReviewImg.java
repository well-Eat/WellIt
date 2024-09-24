package com.wellit.project.shop;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "prod_review_img")
public class ProdReviewImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String imagePath;  // 이미지 경로

    @ManyToOne
    @JoinColumn(name = "rev_id")
//    @JsonBackReference
    private ProdReview prodReview;

}

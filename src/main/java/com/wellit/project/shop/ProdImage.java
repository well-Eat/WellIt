package com.wellit.project.shop;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "prod_image")
@Getter
@Setter
public class ProdImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imagePath;  // 이미지 경로

    private Integer prodImageNum; //이미지 순서

    @ManyToOne
    @JoinColumn(name = "prod_id")
    private Product product;   // 어떤 상품의 이미지인지 매핑
}

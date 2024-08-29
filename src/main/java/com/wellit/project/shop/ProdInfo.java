package com.wellit.project.shop;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ProdInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String infoKey; // Key 값
    private String infoValue; // Value 값

    @ManyToOne
    @JoinColumn(name = "prod_id")
    private Product product;
}

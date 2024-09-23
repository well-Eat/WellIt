package com.wellit.project.shop;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.wellit.project.member.Member;
import com.wellit.project.order.OrderItem;
import jakarta.persistence.*;
import jakarta.validation.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prod_review")
@Getter
@Setter
public class ProdReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer revId;

    private String revImg;
    private String revText;

    @Column(name = "rev_rating")
    @Min(value = 0)
    @Max(value = 5)
    private Integer revRating;

    private LocalDateTime createdAt; // 가입 일자
    private LocalDateTime updatedAt; // 수정 일자

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(name = "member_id")
    @JsonBackReference
    private Member author;

    private String writer;  // 추후 삭제하고 Member로 대체

    @ManyToOne
    @JoinColumn(name = "prod_id")
    @JsonIgnore
    private Product product;

    @OneToMany(mappedBy = "prodReview", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProdReviewImg> prodReviewImgList = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;
}

package com.wellit.project.order;

import com.wellit.project.shop.ProdReview;
import com.wellit.project.shop.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Integer quantity;
    private Integer sumOrgPrice; //원래 상품 가격 * 수량
    private Integer sumDiscPrice; //해당 상품의 할인 금액(음수)

    private boolean reviewed = false; //리뷰 작성 완료 여부

    private LocalDateTime createdAt; //주문 일자

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(name = "order_id")
    private PurchaseOrder purchaseOrder;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProdReview prodReview;
}

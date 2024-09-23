package com.wellit.project.order;

import com.wellit.project.shop.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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


    @ManyToOne
    @JoinColumn(name = "order_id")
    private PurchaseOrder purchaseOrder;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}

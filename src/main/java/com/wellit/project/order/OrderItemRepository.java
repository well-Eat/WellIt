package com.wellit.project.order;

import com.wellit.project.shop.ProdReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}

package com.wellit.project.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    public List<OrderItem> findAllByProduct_ProdId(Long prodId);

}

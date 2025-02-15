package com.wellit.project.order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    public Delivery findDeliveryByPurchaseOrder_OrderId(String orderId);
}

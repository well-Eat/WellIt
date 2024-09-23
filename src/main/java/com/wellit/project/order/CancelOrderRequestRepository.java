package com.wellit.project.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CancelOrderRequestRepository extends JpaRepository<CancelOrderRequest, Long> {

    public Optional<CancelOrderRequest> findByOrderId(String orderId);
}

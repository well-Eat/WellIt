package com.wellit.project.order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    public Payment findPaymentByPurchaseOrder_OrderId(String orderId);

    public Payment findByImpUid(String impUid);
}

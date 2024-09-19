package com.wellit.project.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, String> {

    public List<PurchaseOrder> findAllByMember_MemberId(String memberId);

    List<PurchaseOrder> findAllByMember_MemberIdAndStatusNot(String memberId, OrderStatus status);

    public Page<PurchaseOrder> findByOrderIdContaining(String search, Pageable pageable);

    public Page<PurchaseOrder> findByStatus(OrderStatus status, Pageable pageable);

    public PurchaseOrder findByPayment(Payment payment);

    public Page<PurchaseOrder> findAll(Pageable pageable);


}

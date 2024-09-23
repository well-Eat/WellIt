package com.wellit.project.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, String> {

    public List<PurchaseOrder> findAllByMember_MemberId(String memberId);

    List<PurchaseOrder> findAllByMember_MemberIdAndStatusNotOrderByCreatedAtDesc(String memberId, OrderStatus status);

    public Page<PurchaseOrder> findByOrderIdContaining(String search, Pageable pageable);


    public Page<PurchaseOrder> findByStatus(OrderStatus status, Pageable pageable);

    public PurchaseOrder findByPayment(Payment payment);

    public Page<PurchaseOrder> findAll(Pageable pageable);

    public List<PurchaseOrder> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime cutoffTime);

    // 상태와 날짜 범위로 검색 (LocalDateTime 사용)
    Page<PurchaseOrder> findByStatusAndCreatedAtBetween(OrderStatus status, LocalDateTime start, LocalDateTime end, Pageable pageable);

    // 날짜 범위로 검색 (LocalDateTime 사용)
    Page<PurchaseOrder> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);




}

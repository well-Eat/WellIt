package com.wellit.project.order;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, String> {

    public List<PurchaseOrder> findAllByMember_MemberId(String memberId);

    List<PurchaseOrder> findAllByMember_MemberIdAndStatusNot(String memberId, OrderStatus status);


}

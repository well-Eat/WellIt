package com.wellit.project.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    public List<OrderItem> findAllByProduct_ProdId(Long prodId);


    // 상품별 판매량 및 매출금액을 집계하는 쿼리
    @Query("SELECT o.product.prodId, SUM(o.quantity), SUM(o.sumOrgPrice + o.sumDiscPrice) " +
            "FROM OrderItem o " +
            "JOIN o.purchaseOrder po " +
            "WHERE po.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY o.product.prodId")
    List<Object[]> findProductSalesByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);




    // 특정 상품의 기간 내 판매 수량과 매출을 조회하는 간단한 쿼리
    @Query("SELECT SUM(o.quantity), SUM(o.sumOrgPrice + o.sumDiscPrice) " +
            "FROM OrderItem o " +
            "WHERE o.product.prodId = :prodId AND o.purchaseOrder.createdAt BETWEEN :startDate AND :endDate")
    List<Object[]> findSalesByProductAndDateRange(
            @Param("prodId") Long prodId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

}

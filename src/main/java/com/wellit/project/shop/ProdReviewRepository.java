package com.wellit.project.shop;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdReviewRepository extends JpaRepository<ProdReview, Integer> {



    public boolean existsProdReviewByOrderItem_Id(Long orderItemId);

    public ProdReview findByOrderItem_Id(Long orderItemId);

    // OrderItem ID 리스트로 ProdReview 조회
    Page<ProdReview> findAllByOrderItemIdIn(List<Long> orderItemIds, Pageable pageable);

    public Integer countByOrderItem_Product_ProdId(Long prodId);

}

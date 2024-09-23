package com.wellit.project.shop;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdReviewRepository extends JpaRepository<ProdReview, Integer> {

    public Page<ProdReview> findAllByProduct(Product product, Pageable pageable);

    public boolean existsProdReviewByOrderItem_Id(Long orderItemId);

    public ProdReview findByOrderItem_Id(Long orderItemId);


}

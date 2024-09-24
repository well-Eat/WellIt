package com.wellit.project.shop;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;


public interface ProductRepository extends JpaRepository<Product, Long> {
    // 상품명으로 검색하고 카테고리로 필터링
    Page<Product> findByProdNameContainingAndProdCate(String prodName, String prodCate, Pageable pageable);

    // 상품명으로만 검색
    Page<Product> findByProdNameContaining(String prodName, Pageable pageable);

    // 카테고리로만 필터링
    Page<Product> findByProdCate(String prodCate, Pageable pageable);

    // 모든 상품을 페이징 처리하여 조회
    Page<Product> findAll(Pageable pageable);
}

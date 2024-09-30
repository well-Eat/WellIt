package com.wellit.project.shop;

import org.springframework.data.domain.Page;

import java.sql.Timestamp;

public interface ProductRepositoryCustom {
    Page<ProductAdminDTO> findProductsByCriteria(
            String category, String itemSort, String sortDirection, int page, int size, Timestamp startDate, Timestamp endDate, String status, String search);
}


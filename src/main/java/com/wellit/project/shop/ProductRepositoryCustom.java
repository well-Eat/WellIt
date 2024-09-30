package com.wellit.project.shop;

import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.util.List;

public interface ProductRepositoryCustom {
    Page<Product> findProductsByCriteria(
            String category, String itemSort, String sortDirection, int page, int size, Timestamp startDate, Timestamp endDate, String status, String search);
}


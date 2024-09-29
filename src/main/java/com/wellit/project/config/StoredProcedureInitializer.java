package com.wellit.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class StoredProcedureInitializer {
    @Autowired
    private JdbcTemplate jdbcTemplate; // Spring JDBC를 사용해 데이터베이스에 연결

    @EventListener(ApplicationReadyEvent.class)
    public void initializeProcedures() {
        createFindProductsProcedure();
        createFindProductsCountProcedure();
    }

    private void createFindProductsProcedure() {
        String sql = "CREATE OR REPLACE PROCEDURE find_products_by_criteria ( " +
                "p_category IN VARCHAR2, " +
                "p_item_sort IN VARCHAR2, " +
                "p_sort_direction IN VARCHAR2, " +
                "p_page IN NUMBER, " +
                "p_size IN NUMBER, " +
                "p_start_date IN DATE, " +
                "p_end_date IN DATE, " +
                "p_status IN VARCHAR2, " +
                "p_search IN VARCHAR2, " +
                "p_cur OUT SYS_REFCURSOR " +
                ") AS " +
                "BEGIN " +
                "    OPEN p_cur FOR " +
                "    SELECT p.prod_id, p.prod_status, p.prod_main_img, p.prod_name, p.prod_desc, " +
                "           p.prod_org_price, p.prod_discount, p.prod_cate, p.prod_final_price, " +
                "           p.prod_stock, p.view_cnt, p.created_at, p.updated_at, " +
                "           COALESCE(COUNT(r.rev_id), 0) AS review_count,  " +
                "           COALESCE(AVG(r.rev_rating), 0) AS review_rating,  " +
                "           COALESCE(COUNT(oi.id), 0) AS sales_count,  " +
                "           COALESCE(SUM(oi.quantity), 0) AS total_sales_quantity,  " +
                "           COALESCE(SUM(oi.sum_org_price + oi.sum_disc_price), 0) AS total_sales_amount " +
                "    FROM product p " +
                "    LEFT JOIN order_item oi ON p.prod_id = oi.product_id " +
                "    LEFT JOIN purchase_order po ON oi.order_id = po.order_id " +
                "        AND oi.created_at >= p_start_date " +
                "        AND oi.created_at < p_end_date + 1 " +
                "    LEFT JOIN prod_review r ON oi.id = r.order_item_id " +
                "    WHERE (p_category = 'all' OR p_category IS NULL OR p.prod_cate = p_category) " +
                "    AND (p_status IS NULL OR p.prod_status = p_status) " +
                "    AND (p_search IS NULL OR LOWER(p.prod_name) LIKE '%' || LOWER(p_search) || '%') " +
                "    GROUP BY p.prod_id, p.prod_status, p.prod_main_img, p.prod_name, p.prod_desc, " +
                "             p.prod_org_price, p.prod_discount, p.prod_cate, p.prod_final_price, " +
                "             p.prod_stock, p.view_cnt, p.created_at, p.updated_at " +
                "    ORDER BY " +
                "        CASE " +
                "            WHEN p_item_sort = 'reviewCount' THEN COALESCE(COUNT(r.rev_id), 0) " +
                "            WHEN p_item_sort = 'reviewRating' THEN COALESCE(AVG(r.rev_rating), 0) " +
                "            WHEN p_item_sort = 'salesCount' THEN COALESCE(COUNT(oi.id), 0) " +
                "            WHEN p_item_sort = 'salesQuantity' THEN COALESCE(SUM(oi.quantity), 0) " +
                "            WHEN p_item_sort = 'salesAmount' THEN COALESCE(SUM(oi.sum_org_price + oi.sum_disc_price), 0) " +
                "            WHEN p_item_sort = 'viewCount' THEN p.view_cnt " +
                "            WHEN p_item_sort = 'highPrice' THEN p.prod_final_price " +
                "            WHEN p_item_sort = 'favoriteCount' THEN (SELECT COALESCE(COUNT(fp.id), 0) FROM favorite_product fp WHERE fp.product_id = p.prod_id) " +
                "            WHEN p_item_sort = 'latest' THEN TO_NUMBER(TO_CHAR(p.created_at, 'YYYYMMDDHH24MISS')) " +
                "            WHEN p_item_sort = 'prodName' THEN NULL " +
                "            ELSE NULL " +
                "        END DESC NULLS LAST, " +
                "        CASE " +
                "            WHEN p_item_sort = 'prodName' THEN p.prod_name " +
                "        END, " +
                "        CASE " +
                "            WHEN p_sort_direction = 'DESC' THEN ' DESC' " +
                "            WHEN p_sort_direction = 'ASC' THEN ' ASC' " +
                "        END NULLS LAST " +
                "    OFFSET (p_page - 1) * p_size ROWS FETCH NEXT p_size ROWS ONLY; " +
                "END;";

        jdbcTemplate.execute(sql);
        System.out.println("find_products_by_criteria 저장 프로시저가 성공적으로 생성되었습니다.");
    }

    private void createFindProductsCountProcedure() {
        String sql = "CREATE OR REPLACE PROCEDURE find_products_by_criteria_count ( " +
                "p_category IN VARCHAR2, " +
                "p_start_date IN DATE, " +
                "p_end_date IN DATE, " +
                "p_status IN VARCHAR2, " +
                "p_search IN VARCHAR2, " +
                "p_total_count OUT NUMBER " +
                ") AS " +
                "BEGIN " +
                "    SELECT COUNT(*) " +
                "    INTO p_total_count " +
                "    FROM product p " +
                "    LEFT JOIN order_item oi ON p.prod_id = oi.product_id " +
                "    LEFT JOIN purchase_order po ON oi.order_id = po.order_id " +
                "        AND oi.created_at >= p_start_date " +
                "        AND oi.created_at < p_end_date + 1 " +
                "    WHERE (p_category = 'all' OR p_category IS NULL OR p.prod_cate = p_category) " +
                "    AND (p_status IS NULL OR p.prod_status = p_status) " +
                "    AND (p_search IS NULL OR LOWER(p.prod_name) LIKE '%' || LOWER(p_search) || '%'); " +
                "END;";

        jdbcTemplate.execute(sql);
        System.out.println("find_products_by_criteria_count 저장 프로시저가 성공적으로 생성되었습니다.");
    }
}

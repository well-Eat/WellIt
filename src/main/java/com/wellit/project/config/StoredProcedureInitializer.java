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

        String sql =
                "CREATE OR REPLACE PROCEDURE find_products_by_criteria ( " +
                        "        p_category IN VARCHAR2, " +
                        "        p_item_sort IN VARCHAR2, " +
                        "        p_page IN NUMBER, " +
                        "        p_size IN NUMBER, " +
                        "        p_start_date IN DATE, " +
                        "        p_end_date IN DATE, " +
                        "        p_status IN VARCHAR2,  " +
                        "        p_search IN VARCHAR2,  " +
                        "        p_cur OUT SYS_REFCURSOR " +
                        ") " +
                        "AS " +
                        "        BEGIN " +
                        "OPEN p_cur FOR " +
                        "SELECT p.prod_id, p.prod_status, p.prod_main_img, p.prod_name, p.prod_desc,  " +
                        "        p.prod_org_price, p.prod_discount, p.prod_cate, p.prod_final_price,  " +
                        "        p.prod_stock, p.view_cnt, p.created_at, p.updated_at,  " +
                        "        COALESCE(COUNT(r.rev_id), 0) AS review_count,  " +
                        "        COALESCE(AVG(r.rev_rating), 0) AS review_rating,  " +
                        "        COALESCE(COUNT(CASE WHEN po.status != 'CANCELLED' THEN oi.id END), 0) AS sales_count, " +
                        "        COALESCE(SUM(CASE WHEN po.status != 'CANCELLED' THEN oi.quantity END), 0) AS total_sales_quantity, " +
                        "        COALESCE(SUM(CASE WHEN po.status != 'CANCELLED' THEN (oi.sum_org_price + oi.sum_disc_price) END), 0) AS total_sales_amount " +
                        "FROM product p " +
                        "LEFT JOIN order_item oi ON p.prod_id = oi.product_id " +
                        "LEFT JOIN purchase_order po ON oi.order_id = po.order_id " +
                        "AND oi.created_at >= p_start_date " +
                        "AND oi.created_at < p_end_date + 1  " +
                        "LEFT JOIN prod_review r ON oi.id = r.order_item_id " +
                        "WHERE (p_category = 'all' OR p_category IS NULL OR p.prod_cate = p_category) " +
                        "AND (p_status IS NULL OR p.prod_status = p_status) " +
                        "AND (p_search IS NULL OR LOWER(p.prod_name) LIKE '%' || LOWER(p_search) || '%' " +
                        "        OR TO_CHAR(p.prod_id) = p_search) " +
                        "GROUP BY p.prod_id, p.prod_status, p.prod_main_img, p.prod_name, p.prod_desc, " +
                        "        p.prod_org_price, p.prod_discount, p.prod_cate, p.prod_final_price, " +
                        "        p.prod_stock, p.view_cnt, p.created_at, p.updated_at " +
                        "ORDER BY " +
                        "CASE " +
                        "WHEN p_item_sort = 'reviewCountDESC' THEN COALESCE(COUNT(r.rev_id), 0) " +
                        "WHEN p_item_sort = 'reviewRatingDESC' THEN COALESCE(AVG(r.rev_rating), 0) " +
                        "WHEN p_item_sort = 'salesCountDESC' THEN COALESCE(COUNT(CASE WHEN po.status != 'CANCELLED' THEN oi.id END), 0) " +
                        "WHEN p_item_sort = 'salesQuantityDESC' THEN COALESCE(SUM(CASE WHEN po.status != 'CANCELLED' THEN oi.quantity END), 0) " +
                        "WHEN p_item_sort = 'salesAmountDESC' THEN COALESCE(SUM(CASE WHEN po.status != 'CANCELLED' THEN (oi.sum_org_price + oi.sum_disc_price) END), 0) " +
                        "WHEN p_item_sort = 'viewCountDESC' THEN p.view_cnt  " +
                        "WHEN p_item_sort = 'highPriceDESC' THEN p.prod_final_price " +
                        "WHEN p_item_sort = 'favoriteCountDESC' THEN (SELECT COALESCE(COUNT(fp.id), 0) FROM favorite_product fp WHERE fp.product_id = p.prod_id) " +
                        "WHEN p_item_sort = 'latestDESC' THEN TO_NUMBER(TO_CHAR(p.created_at, 'YYYYMMDDHH24MISS'))  " +
                        "WHEN p_item_sort = 'prodIdDESC' THEN p.prod_id " +
                        "WHEN p_item_sort = 'prodStockDESC' THEN p.prod_stock   " +
                        "WHEN p_item_sort = 'prodNameDESC' THEN NULL   " +
                        "WHEN p_item_sort = 'prodStatusDESC' THEN NULL   " +
                        "ELSE NULL " +
                        "END DESC NULLS LAST, " +
                        "        CASE " +
                        "WHEN p_item_sort = 'reviewCountASC' THEN COALESCE(COUNT(r.rev_id), 0)   " +
                        "WHEN p_item_sort = 'reviewRatingASC' THEN COALESCE(AVG(r.rev_rating), 0)   " +
                        "WHEN p_item_sort = 'salesCountASC' THEN COALESCE(COUNT(CASE WHEN po.status != 'CANCELLED' THEN oi.id END), 0)       " +
                        "WHEN p_item_sort = 'salesQuantityASC' THEN COALESCE(SUM(CASE WHEN po.status != 'CANCELLED' THEN oi.quantity END), 0)       " +
                        "WHEN p_item_sort = 'salesAmountASC' THEN COALESCE(SUM(CASE WHEN po.status != 'CANCELLED' THEN (oi.sum_org_price + oi.sum_disc_price) END), 0)       " +
                        "WHEN p_item_sort = 'viewCountASC' THEN p.view_cnt                       " +
                        "WHEN p_item_sort = 'highPriceASC' THEN p.prod_final_price               " +
                        "WHEN p_item_sort = 'favoriteCountASC' THEN (SELECT COALESCE(COUNT(fp.id), 0) FROM favorite_product fp WHERE fp.product_id = p.prod_id)   " +
                        "WHEN p_item_sort = 'latestASC' THEN TO_NUMBER(TO_CHAR(p.created_at, 'YYYYMMDDHH24MISS'))  " +
                        "WHEN p_item_sort = 'prodIdASC' THEN p.prod_id   " +
                        "WHEN p_item_sort = 'prodStockASC' THEN p.prod_stock   " +
                        "WHEN p_item_sort = 'prodNameASC' THEN NULL   " +
                        "WHEN p_item_sort = 'prodStatusASC' THEN NULL " +
                        "END ASC NULLS LAST, " +
                        "        CASE " +
                        "WHEN p_item_sort = 'prodNameDESC' THEN p.prod_name " +
                        "WHEN p_item_sort = 'prodStatusDESC' THEN p.prod_status " +
                        "END DESC NULLS LAST, " +
                        "        CASE " +
                        "WHEN p_item_sort = 'prodNameASC' THEN p.prod_name " +
                        "WHEN p_item_sort = 'prodStatusASC' THEN p.prod_status " +
                        "END ASC NULLS LAST " +
                        "OFFSET (p_page - 1) * p_size ROWS FETCH NEXT p_size ROWS ONLY; " +
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

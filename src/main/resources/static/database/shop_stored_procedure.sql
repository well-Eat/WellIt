---------------------------------------------------
--
---------------------------------------------------
-- 권한 부족으로 프로시저 create 안되는 경우
-- 관리자 계정 접속 -> GRANT create PROCEDURE TO c##dbexam;

-- 99999. 소팅 프로시저 START ##########################
CREATE OR REPLACE PROCEDURE find_products_by_criteria (
    p_category IN VARCHAR2,
    p_item_sort IN VARCHAR2,
    p_page IN NUMBER,
    p_size IN NUMBER,
    p_cur OUT SYS_REFCURSOR
)
AS
BEGIN
    OPEN p_cur FOR
    SELECT p.prod_id, p.prod_status, p.prod_main_img, p.prod_name, p.prod_desc,
           p.prod_org_price, p.prod_discount, p.prod_cate, p.prod_final_price,
           p.prod_stock, p.view_cnt, p.created_at, p.updated_at,
           COALESCE(COUNT(r.rev_id), 0) AS review_count,  -- 리뷰 수
           COALESCE(AVG(r.rev_rating), 0) AS review_rating,  -- 리뷰 평점
           COALESCE(COUNT(oi.id), 0) AS sales_count  -- 주문 건수
    FROM product p
    LEFT JOIN order_item oi ON p.prod_id = oi.product_id
    LEFT JOIN prod_review r ON oi.id = r.order_item_id
    WHERE (p_category = 'all' OR p_category IS NULL OR p.prod_cate = p_category)
    AND p.prod_status = 'AVAILABLE'
    GROUP BY p.prod_id, p.prod_status, p.prod_main_img, p.prod_name, p.prod_desc,
             p.prod_org_price, p.prod_discount, p.prod_cate, p.prod_final_price,
             p.prod_stock, p.view_cnt, p.created_at, p.updated_at
    ORDER BY
        CASE
            WHEN p_item_sort = 'reviewCount' THEN COALESCE(COUNT(r.rev_id), 0)  -- 리뷰 수 0 처리
            WHEN p_item_sort = 'reviewRating' THEN COALESCE(AVG(r.rev_rating), 0)  -- 리뷰 평점 0 처리
            WHEN p_item_sort = 'salesCount' THEN COALESCE(COUNT(oi.id), 0)  -- 주문 건수 0 처리
            WHEN p_item_sort = 'viewCount' THEN p.view_cnt
            WHEN p_item_sort = 'highPrice' THEN p.prod_final_price
            WHEN p_item_sort = 'favoriteCount' THEN (SELECT COALESCE(COUNT(fp.id), 0) FROM favorite_product fp WHERE fp.product_id = p.prod_id)  -- 찜한 개수 0 처리
            WHEN p_item_sort = 'latest' THEN TO_NUMBER(TO_CHAR(p.created_at, 'YYYYMMDDHH24MISS'))
            ELSE NULL
        END DESC NULLS LAST,
        -- lowPrice만 ASC로 처리
        CASE
            WHEN p_item_sort = 'lowPrice' THEN p.prod_final_price
        END ASC NULLS LAST
    OFFSET (p_page - 1) * p_size ROWS FETCH NEXT p_size ROWS ONLY;

END;
-- 99999. 소팅 프로시저 END ##########################



-- sql developer에서 결과 반환 희망 시 마지막 END 직전에 아래 코드 추가(BUT 웹 작동 안함)
    -- DBMS_SQL.RETURN_RESULT(p_cur);

-- 프로시저 SQL에서 테스트 실행 코드
-- VAR p_cur REFCURSOR; -- 변수 선언
-- EXEC find_products_by_criteria('all', 'reviewCount', 1, 20, :p_cur); -- 프로시저 호출
-- PRINT p_cur;


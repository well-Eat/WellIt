package com.wellit.project.shop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.Timestamp;


import java.util.List;

@Repository
@Log4j2
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public Page<Product> findProductsByCriteria(
            String category, String itemSort, String sortDirection, int page, int size, Timestamp startDate, Timestamp endDate, String status, String search) {


        // 1. 총 레코드 수 계산을 위한 StoredProcedureQuery
        StoredProcedureQuery countQuery = entityManager.createStoredProcedureQuery("find_products_by_criteria_count");
        countQuery.registerStoredProcedureParameter("p_category", String.class, ParameterMode.IN);
        countQuery.registerStoredProcedureParameter("p_start_date", Timestamp.class, ParameterMode.IN);
        countQuery.registerStoredProcedureParameter("p_end_date", Timestamp.class, ParameterMode.IN);
        countQuery.registerStoredProcedureParameter("p_status", String.class, ParameterMode.IN);
        countQuery.registerStoredProcedureParameter("p_search", String.class, ParameterMode.IN);
        countQuery.registerStoredProcedureParameter("p_total_count", Long.class, ParameterMode.OUT);

        // 파라미터 값 설정
        countQuery.setParameter("p_category", category);
        countQuery.setParameter("p_start_date", startDate);
        countQuery.setParameter("p_end_date", endDate);
        countQuery.setParameter("p_status", status);
        countQuery.setParameter("p_search", search);

        // 프로시저 실행 후 총 레코드 수 가져오기
        countQuery.execute();
        Long totalElements = (Long) countQuery.getOutputParameterValue("p_total_count");
        log.info("totalElements: {}", totalElements);
        log.info("페이지당 아이템 : {}", size);
        log.info("현재페이지 : {}", page);
        log.info("총 페이지 수 : {}", Math.ceil(totalElements/size));


        // StoredProcedureQuery 사용
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("find_products_by_criteria", Product.class);

        // 파라미터 설정
        query.registerStoredProcedureParameter("p_category", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_item_sort", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_sort_direction", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_page", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_size", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_start_date", Timestamp.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_end_date", Timestamp.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_status", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_search", String.class, ParameterMode.IN);
        // REF_CURSOR 파라미터 등록
        query.registerStoredProcedureParameter("p_cur", ResultSet.class, ParameterMode.REF_CURSOR);

        // 파라미터 값 설정
        query.setParameter("p_category", category);
        query.setParameter("p_item_sort", itemSort);
        query.setParameter("p_sort_direction", sortDirection);
        query.setParameter("p_page", page);
        query.setParameter("p_size", size);
        query.setParameter("p_start_date", startDate);
        query.setParameter("p_end_date", endDate);
        query.setParameter("p_status", status);
        query.setParameter("p_search", search);

        query.execute();

        // 프로시저 실행 후 결과 반환
        List<Product> products = query.getResultList();

        //페이지 형태로 리턴
        return new PageImpl<>(products, PageRequest.of(page, size), totalElements);
    }
}

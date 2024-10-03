package com.wellit.project.shop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
@Log4j2
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public Page<ProductAdminDTO> findProductsByCriteria(
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
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("find_products_by_criteria");

        // 파라미터 설정
        query.registerStoredProcedureParameter("p_category", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_item_sort", String.class, ParameterMode.IN);
        //query.registerStoredProcedureParameter("p_sort_direction", String.class, ParameterMode.IN);
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
        query.setParameter("p_item_sort", itemSort+sortDirection);
        //query.setParameter("p_sort_direction", sortDirection);
        query.setParameter("p_page", page);
        query.setParameter("p_size", size);
        query.setParameter("p_start_date", startDate);
        query.setParameter("p_end_date", endDate);
        query.setParameter("p_status", status);
        query.setParameter("p_search", search);

        query.execute();

        log.info(query.getParameterValue("p_item_sort"));

        // 프로시저 실행 후 결과 반환
        //List<Product> products = query.getResultList();

        List<Object[]> results = query.getResultList();
        List<ProductAdminDTO> products = new ArrayList<>();

        for (Object[] result : results) {
            ProductAdminDTO dto = new ProductAdminDTO();
            dto.setProdId((Long) result[0]);
            dto.setProdStatus(fromString((String) result[1]));//판매 상태
            dto.setProdMainImg((String) result[2]);
            dto.setProdName((String) result[3]);
            //dto.setProdDesc((String) result[4]);
            // BigDecimal 또는 Integer 처리
            if (result[5] instanceof BigDecimal) {
                dto.setProdOrgPrice(((BigDecimal) result[5]).intValue());
            } else if (result[5] instanceof Integer) {
                dto.setProdOrgPrice((Integer) result[5]);
            }

            if (result[6] instanceof BigDecimal) {
                dto.setProdDiscount(((BigDecimal) result[6]).doubleValue());
            } else if (result[6] instanceof Double) {
                dto.setProdDiscount((Double) result[6]);
            }

            dto.setProdCate((String) result[7]); // 카테고리

            if (result[8] instanceof BigDecimal) {
                dto.setProdFinalPrice(((BigDecimal) result[8]).intValue());
            } else if (result[8] instanceof Integer) {
                dto.setProdFinalPrice((Integer) result[8]);
            }

            if (result[9] instanceof BigDecimal) {
                dto.setProdStock(((BigDecimal) result[9]).longValue());
            } else if (result[9] instanceof Long) {
                dto.setProdStock((Long) result[9]);
            }

            if (result[10] instanceof BigDecimal) {
                dto.setViewCnt(((BigDecimal) result[10]).intValue());
            } else if (result[10] instanceof Integer) {
                dto.setViewCnt((Integer) result[10]);
            }

            Timestamp timestamp1 = (Timestamp) result[11]; // ResultSet에서 받은 Timestamp
            if (timestamp1 != null) {
                dto.setCreatedAt(timestamp1.toLocalDateTime()); // LocalDateTime으로 변환 후 설정
                log.info(dto.getCreatedAt());
            }

            Timestamp timestamp2 = (Timestamp) result[12]; // ResultSet에서 받은 Timestamp
            if (timestamp2 != null) {
                dto.setUpdatedAt(timestamp2.toLocalDateTime()); // LocalDateTime으로 변환 후 설정
            }

            if (result[16] instanceof BigDecimal) {
                dto.setSumQuantity(((BigDecimal) result[16]).intValue());
            } else if (result[16] instanceof Integer) {
                dto.setSumQuantity((Integer) result[16]);
            }

            if (result[17] instanceof BigDecimal) {
                dto.setTotalFinalPrice(((BigDecimal) result[17]).intValue());
            } else if (result[17] instanceof Integer) {
                dto.setTotalFinalPrice((Integer) result[17]);
            }

            products.add(dto);
        }


        //페이지 형태로 리턴
        return new PageImpl<>(products, PageRequest.of(page-1, size), totalElements);
    }
    public static ProdStatus fromString(String status) {
        try {
            return ProdStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            // 해당 문자열이 Enum에 없을 경우 처리 (예: default 값을 반환하거나 예외 처리)
            System.err.println("Unknown status: " + status);
            return null; // 또는 Optional.empty()를 반환하거나, 예외를 던질 수 있습니다.
        }
    }


}

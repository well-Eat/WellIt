package com.wellit.project.store;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreReviewRepository extends JpaRepository<StoreReview, Integer> {
    List<StoreReview> findByStoreId(Integer storeId); // 장소 ID로 리뷰를 찾는 메서드
}
